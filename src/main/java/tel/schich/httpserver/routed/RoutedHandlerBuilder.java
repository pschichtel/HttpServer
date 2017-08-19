package tel.schich.httpserver.routed;

import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import tel.schich.httprequestrouter.RequestRouter;
import tel.schich.httprequestrouter.RouteHandler;
import tel.schich.httprequestrouter.RouteTree;
import tel.schich.httprequestrouter.segment.SegmentOrder;
import tel.schich.httprequestrouter.segment.factory.SegmentFactory;

public class RoutedHandlerBuilder {
    private RequestRouter<HttpMethod, FullHttpRequest, FullHttpResponse> router;

    public RoutedHandlerBuilder(SegmentFactory segmentFactory, SegmentOrder<HttpMethod, FullHttpRequest, FullHttpResponse> order) {
        this.router = new RequestRouter<>(segmentFactory, RouteTree.create(order));
    }

    public RoutedHandlerBuilder addHandler(HttpMethod method, String path, RouteHandler<FullHttpRequest, FullHttpResponse> handler) {
        router = router.withHandler(method, path, handler);
        return this;
    }

    public RoutedHandlerBuilder withHandlersFrom(Class<?>... handlerContainers) {
        for (Class<?> handlerContainer : handlerContainers) {
            RouteHandlerExtractor.extractHandlers(handlerContainer, null /* TODO */);
        }
        return this;
    }
}
