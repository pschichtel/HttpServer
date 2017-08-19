package tel.schich.httpserver.routed;

import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import tel.schich.httprequestrouter.RouteHandler;

public interface RoutedHandlerBuilderLike {
    RoutedHandlerBuilderLike addHandler(HttpMethod method, String path, RouteHandler handler);

    default RoutedHandlerBuilderLike addHandlersFrom(Class<?>... containers) {
        for (Class<?> container : containers) {
            RouteHandlerExtractor.extractHandlers(container, this);
        }
        return this;
    }
}
