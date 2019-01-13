package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import java.awt.Image
import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber

class MapVerticle : AbstractVerticle() {

    private val eventBus by lazy { vertx.eventBus() }

    private val zoomFactors = mapOf(3 to 8, 4 to 16, 5 to 32) // Numbers that are divisible by 8160 without remainder
    private val scaleFactors = mapOf(3 to 0.5, 4 to 0.8, 5 to 1.0)
    private val scaleTypes = mapOf(3 to Image.SCALE_FAST, 4 to Image.SCALE_SMOOTH, 5 to 0)

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

            zoomFactors.forEach { zoom, zoomFactor ->
                val zoomFolder = File("${revisionMapsFolder.path}/$zoom").apply { mkdir() }
                createSubImages(generatedImg, zoomFolder.path, zoomFactor, scaleFactors[zoom]!!, scaleTypes[zoom]!!)
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

    private fun createSubImages(img: BufferedImage, zoomFolderPath: String, zoomFactor: Int, scaleFactor: Double, scaleType: Int) {
        val imageToCrop = if (scaleFactor != 1.0) {
            val scaleSize = (img.width * scaleFactor).toInt()
            val scaledImage = img.getScaledInstance(scaleSize, scaleSize, scaleType)

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
