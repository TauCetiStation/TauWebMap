package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.logging.LoggerFactory
import java.io.File

class MapVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MapVerticle::class.java)
    private val eventBus by lazy { vertx.eventBus() }

    private var currentRevision: String? = null

    init {
        File(MAPS_FOLDER).mkdir()
    }

    override fun start() {
        eventBus.localConsumer<String>(EB_MAP_REVISION_UPDATE) { msg ->
            val revision = msg.body()

            if (revision != currentRevision) {
                logger.info("Generating images for revision $revision")
                generateRevisionImages(CURRENT_MAP_FOLDER)
                logger.info("Images generated")
                currentRevision = revision
            }

            eventBus.send(EB_VIEW_REVISION_UPDATE, revision)
            msg.reply(null)
        }

        eventBus.localConsumer<Void>(EB_MAP_REVISION_HISTORY_CREATE) {
            createHistoryRevisionImages(javaClass.classLoader.getResource(REVISION_HISTORY_FILE).readText().lines().iterator())
        }
    }

    private fun createHistoryRevisionImages(iterator: Iterator<String>) {
        if (iterator.hasNext()) {
            val revision = iterator.next().split(" ")[1]
            val revisionMapsPath = "$MAPS_FOLDER/$revision"

            if (!File(revisionMapsPath).exists()) {
                eventBus.send<Void>(EB_REPO_CHECKOUT, revision) {
                    logger.info("Generating images for history revision $revision")
                    generateRevisionImages("$MAPS_FOLDER/$revision")
                    logger.info("History images generated")
                    createHistoryRevisionImages(iterator)
                }
            } else {
                createHistoryRevisionImages(iterator)
            }
        }
    }

    private fun generateRevisionImages(mapFolderPath: String) {
        File(mapFolderPath).mkdirs()
        ProcessBuilder("java", "-Xms1g", "-Xmx1g", "-jar", "render.jar", mapFolderPath).start().waitFor()
    }
}
