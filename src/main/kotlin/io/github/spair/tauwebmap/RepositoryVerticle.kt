package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.logging.LoggerFactory
import java.io.File
import java.io.InputStreamReader

class RepositoryVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(RepositoryVerticle::class.java)

    override fun start(startFuture: Future<Void>) {
        vertx.executeBlocking<String>({ future ->
            rebaseRepository()
            future.complete(getMapRevision())
        }, {
            if (it.succeeded()) {
                startFuture.complete()
                logger.info("Repository synchronised")
                vertx.eventBus().send(EB_MAP_REV_UPD, it.result())
            } else {
                logger.error("Unable to synchronise repository")
                startFuture.fail(it.cause())
            }
        })
    }

    private fun rebaseRepository() = ProcessBuilder("git", "pull", "--rebase").directory(File(REPO_FOLDER)).start().waitFor()

    private fun getMapRevision(): String {
        return ProcessBuilder("git", "log", "-1", "--pretty=format:\"%h\"", "maps/z1.dmm").directory(File(REPO_FOLDER)).start().let {
            it.waitFor()
            InputStreamReader(it.inputStream).use { s -> s.readText() }
        }
    }
}
