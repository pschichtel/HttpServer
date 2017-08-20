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

import io.netty.handler.codec.http.HttpMethod;
import tel.schich.httprequestrouter.RequestRouter;
import tel.schich.httprequestrouter.RouteTree;
import tel.schich.httprequestrouter.segment.SegmentOrder;
import tel.schich.httprequestrouter.segment.factory.SegmentFactory;

public class MutableRoutingHandler extends RoutingHandler implements HandlerSink {
    private volatile RequestRouter<RouteHandler> router;

    public MutableRoutingHandler(SegmentFactory segmentFactory, SegmentOrder<RouteHandler> order, FallbackHandler notFoundHandler) {
        super(notFoundHandler);
        router = new RequestRouter<>(segmentFactory, RouteTree.create(order));
    }

    @Override
    public RequestRouter<RouteHandler> getRouter() {
        return router;
    }

    @Override
    public synchronized MutableRoutingHandler addHandler(HttpMethod method, String route, RouteHandler handler) {
        router = router.withHandler(method.name(), route, handler);
        return this;
    }
}
