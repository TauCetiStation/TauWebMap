package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class RepositoryVerticle : AbstractVerticle() {

    private val eventBus by lazy { vertx.eventBus() }

    override fun start() {
        rebaseRepository()

        eventBus.send<Void>(EB_MAP_REVISION_UPDATE, getMapRevision()) {
            eventBus.send(EB_MAP_REVISION_HISTORY_CREATE, null)
        }

        vertx.setPeriodic(TimeUnit.HOURS.toMillis(24)) {
            rebaseRepository()
            eventBus.send(EB_MAP_REVISION_UPDATE, getMapRevision())
        }

        eventBus.localConsumer<String>(EB_REPO_CHECKOUT) {
            checkoutTo(it.body())
            it.reply(null)
        }
    }

    private fun rebaseRepository() {
        checkoutTo("master")
        ProcessBuilder("git", "pull", "--rebase").startLocal()
    }

    private fun checkoutTo(commit: String) {
        ProcessBuilder("git", "checkout", commit).startLocal()
    }

    private fun getMapRevision(): String {
        return ProcessBuilder("git", "log", "-1", "--pretty=format:%h", "maps/z1.dmm").startLocal().let {
            InputStreamReader(it.inputStream).use { s -> s.readText() }
        }
    }

    private fun ProcessBuilder.startLocal() = directory(File(REPO_FOLDER)).start().apply { waitFor() }
}
