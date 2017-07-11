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

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static tel.schich.httpserver.HttpServerUtil.simpleHandler;

public class HttpServerBuilder {
    private SocketAddress bindAddress;
    private int maximumContentLength;
    private Supplier<EventLoopGroup> eventLoopGroup;
    private Class<? extends ServerSocketChannel> socketChannelType;
    private final Consumer<SocketChannel> configure;

    public HttpServerBuilder(Function<FullHttpRequest, FullHttpResponse> handler) {
        this(simpleHandler(handler));
    }

    public HttpServerBuilder(ChannelInboundHandler inboundHandler) {
        this((Consumer<SocketChannel>) ch -> ch.pipeline().addLast(inboundHandler));
    }

    public HttpServerBuilder(Consumer<SocketChannel> configure) {
        this.bindAddress = new InetSocketAddress(InetAddress.getLoopbackAddress(), 9000);
        this.maximumContentLength = 1048576;
        this.eventLoopGroup = NioEventLoopGroup::new;
        this.socketChannelType = NioServerSocketChannel.class;
        this.configure = configure;
    }

    public HttpServer build() {
        return new HttpServer(eventLoopGroup.get(), socketChannelType, bindAddress, maximumContentLength, configure);
    }
}
