package io.github.shinglem.easyvertx.web.core.impl;

import io.vertx.core.http.HttpMethod;

import java.lang.annotation.*;


/**
 * @author Shingle
 */
@Repeatable(Route.Routes.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Route {


    interface HttpMethodBase{

    }
    enum Method{
        OPTIONS(HttpMethod.OPTIONS),
        GET(HttpMethod.GET),
        HEAD(HttpMethod.HEAD),
        POST(HttpMethod.POST),
        PUT(HttpMethod.PUT),
        DELETE(HttpMethod.DELETE),
        TRACE(HttpMethod.TRACE),
        CONNECT(HttpMethod.CONNECT),
        PATCH(HttpMethod.PATCH),
        PROPFIND(HttpMethod.PROPFIND),
        PROPPATCH(HttpMethod.PROPPATCH),
        MKCOL(HttpMethod.MKCOL),
        COPY(HttpMethod.COPY),
        MOVE(HttpMethod.MOVE),
        LOCK(HttpMethod.LOCK),
        UNLOCK(HttpMethod.UNLOCK),
        MKCALENDAR(HttpMethod.MKCALENDAR),
        VERSION_CONTROL(HttpMethod.VERSION_CONTROL),
        REPORT(HttpMethod.REPORT),
        CHECKIN(HttpMethod.CHECKIN),
        CHECKOUT(HttpMethod.CHECKOUT),
        UNCHECKOUT(HttpMethod.UNCHECKOUT),
        MKWORKSPACE(HttpMethod.MKWORKSPACE),
        UPDATE(HttpMethod.UPDATE),
        LABEL(HttpMethod.LABEL),
        MERGE(HttpMethod.MERGE),
        BASELINE_CONTROL(HttpMethod.BASELINE_CONTROL),
        MKACTIVITY(HttpMethod.MKACTIVITY),
        ORDERPATCH(HttpMethod.ORDERPATCH),
        ACL(HttpMethod.ACL),
        SEARCH(HttpMethod.SEARCH);

        private final HttpMethod method ;

        private Method(HttpMethod method) {
            this.method = method;
        }

        public HttpMethod getMethod() {
            return method;
        }
    }
    /**
     * @return the path
     * @see io.vertx.ext.web.Router#route(String)
     */
    String path() default "/";

    /**
     * @return the path regex
     * @see io.vertx.ext.web.Router#routeWithRegex(String)
     */
    String regex() default "";

    /**
     * @return the HTTP methods
     * @see io.vertx.ext.web.Route#methods()
     */
    Method[] methods() default {};

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
     * @see io.vertx.ext.web.RoutingContext#getAcceptableContentType()
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
         * @see io.vertx.ext.web.Route#handler(io.vertx.core.Handler)
         */
        NORMAL,
        /**
         * A blocking request handler.
         *
         * @see io.vertx.ext.web.Route#blockingHandler(io.vertx.core.Handler)
         */
        BLOCKING,
        /**
         * A failure handler can declare a single method parameter whose type extends {@link Throwable}. The type of the
         * parameter is used to match the result of {@link io.vertx.ext.web.RoutingContext#failure()}.
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
         * @see io.vertx.ext.web.Route#failureHandler(io.vertx.core.Handler)
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

