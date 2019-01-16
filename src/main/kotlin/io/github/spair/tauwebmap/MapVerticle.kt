package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.logging.LoggerFactory
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

class MapVerticle : AbstractVerticle() {

    private val logger = LoggerFactory.getLogger(MapVerticle::class.java)
    private val eventBus by lazy { vertx.eventBus() }

    private val zoomFactors = mapOf(3 to 8, 4 to 16, 5 to 32) // Numbers that are divisible by 8160 without remainder
    private val scaleFactors = mapOf(3 to 0.5, 4 to 0.8, 5 to 1.0)
    private val compressOptions = arrayOf("--ext=.png", "--force", "--strip", "--speed=1", "--nofs", "--posterize=2")

    private var currentRevision: String? = null

    init {
        File(MAPS_FOLDER).mkdir()
    }

    override fun start() {
        eventBus.localConsumer<String>(EB_MAP_REVISION_UPDATE) { msg ->
            val revision = msg.body()

            if (currentRevision == null || revision != currentRevision) {
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
            eventBus.send<Void>(EB_REPO_CHECKOUT, revision) {
                logger.info("Generating images for history revision $revision")
                generateRevisionImages("$MAPS_FOLDER/$revision")
                logger.info("History images generated")
                createHistoryRevisionImages(iterator)
            }
        }
    }

    private fun generateRevisionImages(mapFolderPath: String) {
        val mapFolder = File(mapFolderPath).apply { mkdir() }
        val generatedImg = ImageIO.read(generateMapImage())

        zoomFactors.forEach { zoom, zoomFactor ->
            val zoomFolder = File("$mapFolderPath/$zoom").apply { mkdir() }
            createSubImages(generatedImg, zoomFolder.path, zoomFactor, scaleFactors[zoom]!!)
        }

        Files.list(mapFolder.toPath()).map(Path::toFile).collect(Collectors.toList()).forEach { zoomFolder ->
            Thread {
                zoomFolder.walk().forEach { imgFile -> compressImage(imgFile.path) }
            }.start()
        }
    }

    private fun generateMapImage(): File {
        ProcessBuilder(
            "java", "-Xms768m", "-Xmx768m", "-jar", "libs/dmmimg.jar",
            "$REPO_FOLDER/taucetistation.dme", "$REPO_FOLDER/maps/z1.dmm",
            "-F=IGNORE", "-T=/turf/space", "-T=/area", "-T=/obj/effect/landmark",
            "-C=libs/render_config.json", "-O=tmp/mapimg.png").start().waitFor()
        return File("tmp/mapimg.png")
    }

    private fun compressImage(imagePath: String) = ProcessBuilder("pngquant", *compressOptions, imagePath).start()

    private fun createSubImages(img: BufferedImage, zoomFolderPath: String, zoomFactor: Int, scaleFactor: Double) {
        val imageToCrop = if (scaleFactor != 1.0) {
            val scaleSize = (img.width * scaleFactor).toInt()
            val scaledImage = img.getScaledInstance(scaleSize, scaleSize, Image.SCALE_SMOOTH)

            BufferedImage(scaleSize, scaleSize, BufferedImage.TYPE_INT_ARGB).apply {
                createGraphics().run {
                    drawImage(scaledImage, 0, 0, null)
                    dispose()
                }
            }
        } else {
            img
        }

        val imageSize = imageToCrop.width / zoomFactor
        for (x in 0 until zoomFactor) {
            for (y in 0 until zoomFactor) {
                imageToCrop.getSubimage(x * imageSize, y * imageSize, imageSize, imageSize).run {
                    if (!isBlankImage(this)) {
                        ImageIO.write(this, "png", File("$zoomFolderPath/$y-$x.png"))
                    }
                }
            }
        }
    }

    private fun isBlankImage(img: BufferedImage): Boolean {
        val w = img.width
        val h = img.height
        val pixels = IntArray(w * h)

        PixelGrabber(img, 0, 0, w, h, pixels, 0, w).run { grabPixels() }

        var isBlank = true
        for (pixel in pixels) {
            if (pixel != 0) {
                isBlank = false
                break
            }
        }

        return isBlank
    }
}
