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

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.util.concurrent.CompletableFuture;

public class Response {
    private final ChannelHandlerContext context;
    private final HttpVersion version;
    private final HttpResponseStatus status = HttpResponseStatus.OK;

    public Response(ChannelHandlerContext context, HttpRequest req) {
        this.context = context;
        this.version = req.protocolVersion();
    }

    public CompletableFuture<Void> send() {
        CompletableFuture<Void> done = new CompletableFuture<>();
        context.write(new DefaultFullHttpResponse(version, status))
                .addListener(ChannelFutureListener.CLOSE)
                .addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) done.complete(null);
                    else done.completeExceptionally(channelFuture.cause());
                });
        return done;
    }

    public void upgrade(ChannelInboundHandler inboundHandler) {
        context.pipeline()
                .remove(context.handler())
                .addLast(inboundHandler);
    }
}
