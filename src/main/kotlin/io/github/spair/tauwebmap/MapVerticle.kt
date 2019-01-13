package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber

class MapVerticle : AbstractVerticle() {

    private val eventBus by lazy { vertx.eventBus() }

    private val initZoomLevel = 3
    private val scaleFactorList = listOf(8, 16, 32, 68) // Numbers that are divisible by 8160 without remainder
    private val compressOptions = arrayOf("--ext=.png", "--force", "--strip", "--speed=1", "--nofs", "--posterize=2")

    init {
        File(MAPS_FOLDER).mkdir()
    }

    override fun start() {
        eventBus.localConsumer<String>(EB_MAP_REV_UPD, updateRevision())
    }

    private fun updateRevision() = Handler<Message<String>> { msg ->
        val revision = msg.body()
        val revisionMapsFolder = File("$MAPS_FOLDER/$revision")

        if (!revisionMapsFolder.exists()) {
            revisionMapsFolder.mkdir()
            val generatedImg = ImageIO.read(generateMapImage())

            scaleFactorList.forEachIndexed { index, scaleFactor ->
                val zoomFolder = File("${revisionMapsFolder.path}/${index + initZoomLevel}").apply { mkdir() }
                createSubImages(generatedImg, zoomFolder.path, scaleFactor)
            }

            revisionMapsFolder.walk().maxDepth(1).forEach { zoomFolder ->
                if (zoomFolder.name.length > 1) // Skip root folder
                    return@forEach

                Thread {
                    zoomFolder.walk().forEach { imgFile -> compressImage(imgFile.path) }
                }.start()
            }
        }

        eventBus.send(EB_VIEW_REV_UPD, revision)
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

    private fun createSubImages(img: BufferedImage, zoomFolderPath: String, scaleFactor: Int) {
        val imageSize = img.width / scaleFactor
        for (x in 0 until scaleFactor) {
            for (y in 0 until scaleFactor) {
                img.getSubimage(x * imageSize, y * imageSize, imageSize, imageSize).run {
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
