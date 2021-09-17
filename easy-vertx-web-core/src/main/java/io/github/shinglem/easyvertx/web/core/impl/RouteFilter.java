package io.github.shinglem.easyvertx.web.core.impl;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vertx.ext.web.RoutingContext;


/**
 * @author Shingle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RouteFilter {

    static final int DEFAULT_PRIORITY = 10;


    int value() default DEFAULT_PRIORITY;

    Route[] routers() default {};

}
