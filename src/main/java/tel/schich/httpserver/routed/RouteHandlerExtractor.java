package tel.schich.httpserver.routed;

import io.netty.handler.codec.http.HttpMethod;

import java.lang.reflect.Method;

public class RouteHandlerExtractor {
    public static void extractHandlers(Class<?> container, RoutedHandlerBuilderLike target) {
        for (Method method : container.getDeclaredMethods()) {
            HandleRoute annotation = method.getAnnotation(HandleRoute.class);
            if (annotation == null) {
                continue;
            }

            HttpMethod verb = HttpMethod.valueOf(annotation.method());
            if (verb == null) {
                continue;
            }

            Class<?>[] parameterTypes = method.getParameterTypes();

        }
    }
}
