package io.github.spair.tauwebmap

import io.github.spair.tauwebmap.util.reporter
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Verticle
import io.vertx.core.CompositeFuture
import io.vertx.core.DeploymentOptions
import io.vertx.core.logging.LoggerFactory
import java.io.File

class MainVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)

    init {
        File(DATA_FOLDER).mkdir()
        File(TMP_FOLDER).mkdir()
    }

    override fun start(startFuture: Future<Void>) {
        deployVerticles(startFuture)
    }

    private fun deployVerticles(future: Future<Void>) {
        fun initVerticle(verticle: Verticle, worker: Boolean = false): Future<Void> = Future.future<Void>().apply {
            vertx.deployVerticle(verticle, DeploymentOptions().setWorker(worker), reporter(this) {
                logger.info("Verticle '${verticle.javaClass.simpleName}' deployed")
            })
        }

        val verticlesList = listOf(
            initVerticle(ViewVerticle()),
            initVerticle(MapGenerationVerticle(), true),
            initVerticle(RepositoryVerticle(), true)
        )

        logger.info("Deploying verticles...")
        CompositeFuture.all(verticlesList).setHandler(reporter(future) {
            logger.info("All verticles deployed (${verticlesList.size})")
        })
    }
}
