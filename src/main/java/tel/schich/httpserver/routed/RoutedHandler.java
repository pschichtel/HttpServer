package tel.schich.httpserver.routed;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import tel.schich.httprequestrouter.RequestRouter;
import tel.schich.httprequestrouter.RouteFallbackHandler;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class RoutedHandler implements Function<FullHttpRequest, CompletableFuture<FullHttpResponse>> {
    private final RouteFallbackHandler<HttpMethod, FullHttpRequest, FullHttpResponse> notFoundHandler;

    public RoutedHandler(RouteFallbackHandler<HttpMethod, FullHttpRequest, FullHttpResponse> notFoundHandler) {
        this.notFoundHandler = notFoundHandler;
    }

    public abstract RequestRouter<HttpMethod, FullHttpRequest, FullHttpResponse> getRouter();

    @Override
    public CompletableFuture<FullHttpResponse> apply(FullHttpRequest req) {
        return getRouter().routeRequest(req.method(), req.uri(), notFoundHandler).apply(req);
    }
}
