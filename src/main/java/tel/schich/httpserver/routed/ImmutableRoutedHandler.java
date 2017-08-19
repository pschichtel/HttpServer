package tel.schich.httpserver.routed;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import tel.schich.httprequestrouter.RequestRouter;
import tel.schich.httprequestrouter.RouteFallbackHandler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ImmutableRoutedHandler extends RoutedHandler {
    private final RequestRouter<HttpMethod, FullHttpRequest, FullHttpResponse> router;

    public ImmutableRoutedHandler(RequestRouter<HttpMethod, FullHttpRequest, FullHttpResponse> router, RouteFallbackHandler<HttpMethod, FullHttpRequest, FullHttpResponse> notFoundHandler) {
        super(notFoundHandler);
        this.router = router;
    }

    public RequestRouter<HttpMethod, FullHttpRequest, FullHttpResponse> getRouter() {
        return router;
    }

}
