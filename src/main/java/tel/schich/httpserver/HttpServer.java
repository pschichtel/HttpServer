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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import sun.misc.Signal;

import java.net.SocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static tel.schich.httpserver.HttpServerUtil.*;

public class HttpServer {
    private final ServerBootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private Channel channel;

    public HttpServer(EventLoopGroup elg, Class<? extends ServerSocketChannel> channelType, SocketAddress bindAddress, int maxContentLength, Consumer<SocketChannel> configure) {
        this.bootstrap = new ServerBootstrap();
        this.eventLoopGroup = elg;
        bootstrap.group(elg)
                .channel(channelType)
                .childHandler(new HttpServerInitializer(maxContentLength, configure))
                .localAddress(bindAddress);
    }

    public synchronized boolean isRunning() {
        return (this.channel != null && this.channel.isOpen());
    }

    /**
     * Starts the server
     */
    public synchronized void start() throws InterruptedException {
        if (!this.isRunning()) {
            try {
                this.channel = bootstrap.bind().sync().channel();
            } catch (InterruptedException e) {
                this.eventLoopGroup.shutdownGracefully(2, 5, TimeUnit.SECONDS);
                throw e;
            }
        }
    }

    /**
     * Stops the server
     */
    public CompletableFuture<?> stop() {
        if (this.isRunning()) {
            this.channel = null;
            return toJavaFuture(this.eventLoopGroup.shutdownGracefully(2, 5, TimeUnit.SECONDS));
        }
        return CompletableFuture.completedFuture(null);
    }

    private static FullHttpResponse handle(FullHttpRequest req) {
        String payload = req.method() + " " + req.uri() + " " + req.protocolVersion() + "\n";
        System.out.print(payload);
        return response(req, OK, payload);
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        HttpServer server = new HttpServerBuilder(HttpServer::handle).build();
        server.start();
        System.out.println("Started");

        Signal.handle(new Signal("INT"), s -> {
            try {
                server.stop().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("Stopped");
        });
    }
}
