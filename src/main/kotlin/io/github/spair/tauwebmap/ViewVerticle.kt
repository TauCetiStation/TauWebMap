package io.github.spair.tauwebmap

import io.github.spair.tauwebmap.util.reporter
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.FaviconHandler
import io.vertx.ext.web.handler.StaticHandler

class ViewVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>) {
        vertx.createHttpServer().requestHandler(Router.router(vertx).apply {
            route().handler(FaviconHandler.create())
            route().handler(StaticHandler.create())
        }).listen(8080, reporter(startFuture))
    }
}