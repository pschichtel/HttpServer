/*
 * The MIT License
 * Copyright © 2017 Phillip Schichtel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package tel.schich.httpserver;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.Future;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static io.netty.channel.ChannelFutureListener.CLOSE;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpServerUtil {
    public static ChannelInboundHandler simpleHandler(Function<FullHttpRequest, FullHttpResponse> handler) {
        return new SimpleFunctionHandler(handler);
    }

    public static FullHttpResponse response(HttpRequest req, HttpResponseStatus status, String content) {
        return new DefaultFullHttpResponse(req.protocolVersion(), status, Unpooled.copiedBuffer(content, UTF_8));
    }


    public static <T> CompletableFuture<T> toJavaFuture(Future<T> netty) {
        final CompletableFuture<T> future = new CompletableFuture<>();

        netty.addListener(f -> {
            if (netty.isSuccess()) {
                future.complete(netty.getNow());
            } else {
                future.completeExceptionally(netty.cause());
            }
        });

        return future;
    }

    @ChannelHandler.Sharable
    private static final class SimpleFunctionHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        private final Function<FullHttpRequest, FullHttpResponse> handler;

        private SimpleFunctionHandler(Function<FullHttpRequest, FullHttpResponse> handler) {
            this.handler = handler;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            ctx.writeAndFlush(handler.apply(msg)).addListener(CLOSE);
        }
    }
}
