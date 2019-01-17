package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServerResponse
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.FaviconHandler
import io.vertx.ext.web.handler.StaticHandler
import java.io.File
import java.net.HttpURLConnection

class ViewVerticle : AbstractVerticle() {

    private var currentRevision: String? = null

    override fun start(startFuture: Future<Void>) {
        vertx.eventBus().localConsumer<String>(EB_VIEW_REVISION_UPDATE) { currentRevision = it.body() }

        vertx.createHttpServer().requestHandler(Router.router(vertx).apply {
            route().handler(FaviconHandler.create())
            route().handler(StaticHandler.create())

            get("/revision").handler { ctx ->
                if (currentRevision == null) {
                    ctx.response().setStatusCode(HttpURLConnection.HTTP_NOT_FOUND).end()
                } else {
                    ctx.response().infoTextHeaders().end(currentRevision)
                }
            }

            get("/revision/history").handler { ctx ->
                ctx.response().infoTextHeaders().end(Buffer.buffer(javaClass.classLoader.getResource(REVISION_HISTORY_FILE).readBytes()))
            }

            get("/tiles/:revision/:zoom/:y/:x").handler { ctx ->
                val revision = ctx.request().getParam("revision")
                val zoom = ctx.request().getParam("zoom")
                val y = ctx.request().getParam("y")
                val x = ctx.request().getParam("x")

                with(ctx.response()) {
                    putHeader(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")

                    val revisionFolderName = if (revision == currentRevision) CURRENT_FOLDER else revision
                    val tilePath = "$MAPS_FOLDER/$revisionFolderName/$zoom/$y-$x.png"

                    if (File(tilePath).exists()) {
                        sendFile(tilePath)
                    } else {
                        setStatusCode(HttpURLConnection.HTTP_NO_CONTENT).end()
                    }
                }
            }
        }).listen(3000, reporter(startFuture))
    }

    private fun HttpServerResponse.infoTextHeaders(): HttpServerResponse {
        return apply {
            putHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
            putHeader(HttpHeaders.CONTENT_TYPE, "text/plain")
        }
    }
}
