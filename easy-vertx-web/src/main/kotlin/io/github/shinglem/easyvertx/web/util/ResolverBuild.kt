package io.github.shinglem.easyvertx.web.util

import io.github.shinglem.easyvertx.core.util.*
import io.github.shinglem.easyvertx.web.core.impl.RawRouter
import io.github.shinglem.easyvertx.web.core.impl.Route
import io.github.shinglem.easyvertx.web.handler.*

@JvmOverloads
fun resolveBuild(
    classHandlers: Set<ClassHandler> = mutableSetOf(RouteBaseHandler.INSTANCE),
    functionHandlers: Set<FunctionHandler> = mutableSetOf(
        RoutesHandler.INSTANCE,
        RouteHandler.INSTANCE,
        RawRouterHandler.INSTANCE
    ),
    parameterHandlers: Set<FunctionParameterHandler> = mutableSetOf(
        QueryParamHandler.INSTANCE,
        PathParamHandler.INSTANCE,
        FormParamHandler.INSTANCE,
        BodyParamHandler.INSTANCE,
        BodyHandler.INSTANCE,
        FormHandler.INSTANCE,
        ParamHandler.INSTANCE,
        ParamsHandler.INSTANCE,
        HeadersHandler.INSTANCE,
        HeaderHandler.INSTANCE,
        RouteContextHandler.INSTANCE,
        HttpRequestHandler.INSTANCE,
        HttpResponseHandler.INSTANCE,
        IdHandler.INSTANCE
    ),
    doFunctionHandlers: Set<DoFunctionHandler> = mutableSetOf(),
    returnHandlers: Set<ReturnHandler> = mutableSetOf(),

    defaultClassHandlers: Set<ClassHandler> = mutableSetOf(),
    defaultFunctionHandlers: Set<FunctionHandler> = mutableSetOf(),
    defaultFunctionParameterHandlers: Set<FunctionParameterHandler> = mutableSetOf(),
    defaultDoFunctionHandlers: Set<DoFunctionHandler> = mutableSetOf(Processor.INSTANCE),
    defaultReturnHandlers: Set<ReturnHandler> = mutableSetOf(DefaultReturnHandler.INSTANCE),

    ) = Resolver(
    classHandlers,
    functionHandlers,
    parameterHandlers,
    doFunctionHandlers,
    returnHandlers,
    defaultClassHandlers,
    defaultFunctionHandlers,
    defaultFunctionParameterHandlers,
    defaultDoFunctionHandlers,
    defaultReturnHandlers,

    listOf(Route::class , Route.Routes::class , RawRouter::class)

)
