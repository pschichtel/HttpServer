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
package tel.schich.httpserver.routed;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import tel.schich.httprequestrouter.RequestRouter;
import tel.schich.httprequestrouter.RoutingResult;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class RoutingHandler implements Function<FullHttpRequest, CompletableFuture<FullHttpResponse>> {
    private final FallbackHandler notFoundHandler;

    public RoutingHandler(FallbackHandler notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
    }

    public abstract RequestRouter<RouteHandler> getRouter();

    @Override
    public CompletableFuture<FullHttpResponse> apply(FullHttpRequest req) {
        RoutingResult<RouteHandler> result = getRouter().routeRequest(req.method().toString(), req.uri());
        Optional<RouteHandler> handler = result.getHandler();
        return handler.orElseGet(() -> r -> notFoundHandler.handle(r, result)).handle(req);
    }
}
