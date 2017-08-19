package tel.schich.httpserver.routed;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HandleRoute {
    String value();
    String method() default "GET";
}

