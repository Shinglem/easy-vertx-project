package io.github.shinglem.easyvertx.web.core.impl;

import java.lang.annotation.*;


/**
 * @author Shingle
 */
@Repeatable(Route.Routes.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {

//    /**
//     * Represents an HTTP method.
//     * This enumeration only provides the common HTTP method.
//     * For custom methods, you need to register the {@code route} manually on the managed {@code Router}.
//     */
//    enum HttpMethod {
//
//        /**
//         * The RFC 2616 `OPTIONS` method, this instance is interned and uniquely used.
//         */
//        OPTIONS,
//
//        /**
//         * The RFC 2616 `GET` method, this instance is interned and uniquely used.
//         */
//        GET,
//
//        /**
//         * The RFC 2616 `HEAD` method, this instance is interned and uniquely used.
//         */
//        HEAD,
//
//        /**
//         * The {RFC 2616 @code POST} method, this instance is interned and uniquely used.
//         */
//        POST,
//
//        /**
//         * The RFC 2616 `PUT` method, this instance is interned and uniquely used.
//         */
//        PUT,
//
//        /**
//         * The RFC 2616 `DELETE` method, this instance is interned and uniquely used.
//         */
//        DELETE,
//
//        /**
//         * The RFC 2616 `TRACE` method, this instance is interned and uniquely used.
//         */
//        TRACE,
//
//        /**
//         * The RFC 2616 `CONNECT` method, this instance is interned and uniquely used.
//         */
//        CONNECT,
//
//        /**
//         * The RFC 5789 `PATCH` method, this instance is interned and uniquely used.
//         */
//        PATCH,
//
//        /**
//         * The RFC 2518/4918 `PROPFIND` method, this instance is interned and uniquely used.
//         */
//        PROPFIND,
//
//        /**
//         * The RFC 2518/4918 `PROPPATCH` method, this instance is interned and uniquely used.
//         */
//        PROPPATCH,
//
//        /**
//         * The RFC 2518/4918 `MKCOL` method, this instance is interned and uniquely used.
//         */
//        MKCOL,
//
//        /**
//         * The RFC 2518/4918 `COPY` method, this instance is interned and uniquely used.
//         */
//        COPY,
//
//        /**
//         * The RFC 2518/4918 `MOVE` method, this instance is interned and uniquely used.
//         */
//        MOVE,
//
//        /**
//         * The RFC 2518/4918 `LOCK` method, this instance is interned and uniquely used.
//         */
//        LOCK,
//
//        /**
//         * The RFC 2518/4918 `UNLOCK` method, this instance is interned and uniquely used.
//         */
//        UNLOCK,
//
//        /**
//         * The RFC 4791 `MKCALENDAR` method, this instance is interned and uniquely used.
//         */
//        MKCALENDAR,
//
//        /**
//         * The RFC 3253 `VERSION_CONTROL` method, this instance is interned and uniquely used.
//         */
//        VERSION_CONTROL,
//
//        /**
//         * The RFC 3253 `REPORT` method, this instance is interned and uniquely used.
//         */
//        REPORT,
//
//        /**
//         * The RFC 3253 `CHECKOUT` method, this instance is interned and uniquely used.
//         */
//        CHECKOUT,
//
//        /**
//         * The RFC 3253 `CHECKIN` method, this instance is interned and uniquely used.
//         */
//        CHECKIN,
//
//        /**
//         * The RFC 3253 `UNCHECKOUT` method, this instance is interned and uniquely used.
//         */
//        UNCHECKOUT,
//
//        /**
//         * The RFC 3253 `MKWORKSPACE` method, this instance is interned and uniquely used.
//         */
//        MKWORKSPACE,
//
//        /**
//         * The RFC 3253 `UPDATE` method, this instance is interned and uniquely used.
//         */
//        UPDATE,
//
//        /**
//         * The RFC 3253 `LABEL` method, this instance is interned and uniquely used.
//         */
//        LABEL,
//
//        /**
//         * The RFC 3253 `MERGE` method, this instance is interned and uniquely used.
//         */
//        MERGE,
//
//        /**
//         * The RFC 3253 `BASELINE_CONTROL` method, this instance is interned and uniquely used.
//         */
//        BASELINE_CONTROL,
//
//        /**
//         * The RFC 3253 `MKACTIVITY` method, this instance is interned and uniquely used.
//         */
//        MKACTIVITY,
//
//        /**
//         * The RFC 3648 `ORDERPATCH` method, this instance is interned and uniquely used.
//         */
//        ORDERPATCH,
//
//        /**
//         * The RFC 3744 `ACL` method, this instance is interned and uniquely used.
//         */
//        ACL,
//
//        /**
//         * The RFC 5323 `SEARCH` method, this instance is interned and uniquely used.
//         */
//        SEARCH,
//    }

    /**
     * @return the path
     * @see Router#route(String)
     */
    String path() default "/";

    /**
     * @return the path regex
     * @see Router#routeWithRegex(String)
     */
    String regex() default "";

    /**
     * @return the HTTP methods
     * @see io.vertx.ext.web.Route#methods()
     */
    String[] methods() default {};

    /**
     * @return the type of the handler
     */
    HandlerType type() default HandlerType.NORMAL;

    /**
     * If set to a positive number, it indicates the place of the route in the chain.
     *
     * @see io.vertx.ext.web.Route#order(int)
     */
    int order() default 0;

    /**
     * Used for content-based routing.
     * <p>
     * If no {@code Content-Type} header is set then try to use the most acceptable content type.
     * <p>
     * If the request does not contain an 'Accept' header and no content type is explicitly set in the
     * handler then the content type will be set to the first content type in the array.
     *
     * @return the produced content types
     * @see io.vertx.ext.web.Route#produces(String)
     * @see RoutingContext#getAcceptableContentType()
     */
    String[] produces() default {};

    /**
     * Used for content-based routing.
     *
     * @return the consumed content types
     * @see io.vertx.ext.web.Route#consumes(String)
     */
    String[] consumes() default {};

    enum HandlerType {

        /**
         * A non-blocking request handler.
         *
         * @see io.vertx.ext.web.Route#handler(Handler)
         */
        NORMAL,
        /**
         * A blocking request handler.
         *
         * @see io.vertx.ext.web.Route#blockingHandler(Handler)
         */
        BLOCKING,
        /**
         * A failure handler can declare a single method parameter whose type extends {@link Throwable}. The type of the
         * parameter is used to match the result of {@link RoutingContext#failure()}.
         *
         * <pre>
         * <code>
         *  class Routes {
         *     {@literal @Route(type = HandlerType.FAILURE)}
         *     void unsupported(UnsupportedOperationException e, HttpServerResponse response) {
         *        response.setStatusCode(501).end(e.getMessage());
         *     }
         *  }
         *  </code>
         * </pre>
         *
         * <p>
         * If a failure handler declares neither a path nor a regex then the route matches all requests.
         *
         * @see io.vertx.ext.web.Route#failureHandler(Handler)
         */
        FAILURE;

        public static HandlerType from(String value) {
            for (HandlerType handlerType : values()) {
                if (handlerType.toString().equals(value)) {
                    return handlerType;
                }
            }
            return null;
        }

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Routes {

        Route[] value();

    }

}

