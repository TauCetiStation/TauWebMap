package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.FaviconHandler
import io.vertx.ext.web.handler.StaticHandler
import java.io.File
import java.net.HttpURLConnection

const val REVISIONS_FILE = ".revisions"
const val MAPS_FOLDER = "data/maps"

class ViewVerticle : AbstractVerticle() {

    override fun start() {
        vertx.createHttpServer().requestHandler(Router.router(vertx).apply {
            route().handler(FaviconHandler.create())
            route().handler(StaticHandler.create())

            get("/revisions").handler { ctx ->
                ctx.response()
                    .putHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                    .putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
                    .sendFile(REVISIONS_FILE)
            }

            get("/tiles/:revision/:layer/:zoom/:y/:x").handler { ctx ->
                val revision = ctx.request().getParam("revision")
                val zoom = ctx.request().getParam("zoom")
                val layer = ctx.request().getParam("layer")
                val y = ctx.request().getParam("y")
                val x = ctx.request().getParam("x")

                with(ctx.response()) {
                    putHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=2592000")

                    val tilePath = "$MAPS_FOLDER/$revision/$layer/$zoom/$y-$x.png"

                    if (File(tilePath).exists()) {
                        sendFile(tilePath)
                    } else {
                        setStatusCode(HttpURLConnection.HTTP_NO_CONTENT).end()
                    }
                }
            }
        }).listen(3000)
    }
}
