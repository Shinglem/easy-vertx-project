package io.github.shinglem.easyvertx.web.core.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.vertx.core.http.HttpServerRequest;

/**
 * Identifies a route method parameter that should be injected with a value returned from
 * {@link HttpServerRequest#getHeader(String)}.
 * <p>
 * The parameter type must be {@link String}, {@code java.util.Optional<String>} or {@code java.util.List<String>}, otherwise
 * the build fails.
 *
 * @author Shingle
 * @see HttpServerRequest#getHeader(String)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Headers {

}
