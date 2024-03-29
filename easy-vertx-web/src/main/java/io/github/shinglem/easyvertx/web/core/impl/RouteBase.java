package io.github.shinglem.easyvertx.web.core.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Shingle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RouteBase {

    /**
     * The value is used as a prefix for any route method declared on the class where {@link Route#path()} is used.
     *
     * @return the path prefix
     */
    String path() default "/";

    /**
     * The values are used for any route method declared on the class where {@link Route#produces()} is empty.
     *
     * @return the produced content types
     */
    String[] produces() default {};

    /**
     * The values are used for any route method declared on the class where {@link Route#consumes()} is empty.
     *
     * @return the consumed content types
     */
    String[] consumes() default {};

}
