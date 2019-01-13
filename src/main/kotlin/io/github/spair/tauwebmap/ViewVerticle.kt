package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpHeaders
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.FaviconHandler
import io.vertx.ext.web.handler.StaticHandler
import java.io.File

class ViewVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(ViewVerticle::class.java)
    private val eventBus by lazy { vertx.eventBus() }
    private lateinit var currentRevision: String

    override fun start(startFuture: Future<Void>) {
        eventBus.localConsumer<String>(EB_VIEW_REV_UPD) {
            currentRevision = it.body()
            logger.info("Current map revision: $currentRevision")
        }

        vertx.createHttpServer().requestHandler(Router.router(vertx).apply {
            route().handler(FaviconHandler.create())
            route().handler(StaticHandler.create())

            get("/revision").handler { ctx ->
                ctx.response().end(currentRevision)
            }

            get("/tiles/:revision/:zoom/:y/:x").handler { ctx ->
                val revision = ctx.request().getParam("revision")
                val zoom = ctx.request().getParam("zoom")
                val y = ctx.request().getParam("y")
                val x = ctx.request().getParam("x")
                val tilePath = "$MAPS_FOLDER/$revision/$zoom/$y-$x.png"

                with(ctx.response()) {
                    putHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                    if (File(tilePath).exists()) {
                        sendFile("$MAPS_FOLDER/$revision/$zoom/$y-$x.png")
                    } else {
                        end()
                    }
                }
            }
        }).listen(8080, reporter(startFuture))
    }
}
