package tel.schich.httpserver.routed;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import tel.schich.httprequestrouter.RequestRouter;
import tel.schich.httprequestrouter.RouteFallbackHandler;
import tel.schich.httprequestrouter.RouteHandler;
import tel.schich.httprequestrouter.RouteTree;
import tel.schich.httprequestrouter.segment.SegmentOrder;
import tel.schich.httprequestrouter.segment.factory.SegmentFactory;

public class MutableRoutedHandler extends RoutedHandler {
    private volatile RequestRouter<HttpMethod, FullHttpRequest, FullHttpResponse> router;

    public MutableRoutedHandler(SegmentFactory segmentFactory, SegmentOrder<HttpMethod, FullHttpRequest, FullHttpResponse> order, RouteFallbackHandler<HttpMethod, FullHttpRequest, FullHttpResponse> notFoundHandler) {
        super(notFoundHandler);
        router = new RequestRouter<>(segmentFactory, RouteTree.create(order));
    }

    @Override
    public RequestRouter<HttpMethod, FullHttpRequest, FullHttpResponse> getRouter() {
        return router;
    }

    public synchronized MutableRoutedHandler addHandler(HttpMethod method, String route, RouteHandler<FullHttpRequest, FullHttpResponse> handler) {
        router = router.withHandler(method, route, handler);
        return this;
    }
}
