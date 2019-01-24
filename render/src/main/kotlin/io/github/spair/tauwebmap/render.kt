package io.github.spair.tauwebmap

import io.github.spair.byond.dme.parser.DmeParser
import io.github.spair.byond.dmm.Dmm
import io.github.spair.byond.dmm.drawer.DmmDrawer
import io.github.spair.byond.dmm.drawer.FilterMode
import io.github.spair.dmm.io.reader.DmmReader
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber
import java.io.File
import javax.imageio.ImageIO

private const val REVISIONS = ".revisions"

private const val REPO_PATH = "tmp/repo"
private const val DME_PATH = "$REPO_PATH/taucetistation.dme"
private const val DMM_PATH = "$REPO_PATH/maps/z1.dmm"

private val IGNORE_TYPES = arrayOf("/turf/space", "/area", "/obj/effect/landmark")
private val COMPRESSION_ARGS =
    arrayOf("pngquant", "--ext=.png", "--force", "--strip", "--speed=1", "--nofs", "--posterize=2")

// Numbers that are divisible by 8160 without remainder
private val zoomFactors = mapOf(3 to 8, 4 to 16, 5 to 32)
private val scaleFactors = mapOf(3 to 0.3, 4 to 0.6, 5 to 1.0)

fun main() {
    File(REVISIONS).forEachLine { line ->
        val revision = line.split(" ".toRegex())[1]
        ProcessBuilder("git", "checkout", revision).directory(File(REPO_PATH)).start().waitFor()
        print("Rendering for $revision...")
        render("data/maps/$revision")
        println(" Done!")
    }
}

private fun render(mapFolderPath: String) {
    val generatedImg = generateMapImage()

    zoomFactors.forEach { zoom, zoomFactor ->
        val zoomFolder = File("$mapFolderPath/$zoom")
        zoomFolder.mkdirs()
        createSubImages(generatedImg, zoomFolder.path, zoomFactor, scaleFactors[zoom]!!)
    }

    val mapFolder = File(mapFolderPath)

    mapFolder.listFiles().forEach { zoomFolder ->
        Thread {
            zoomFolder.listFiles().forEach { imgFile ->
                ProcessBuilder(*COMPRESSION_ARGS, imgFile.path).start().waitFor()
            }
        }.start()
    }
}

private fun generateMapImage(): BufferedImage {
    class Resource

    val dme = DmeParser.parse(File(DME_PATH))
    dme.mergeWithJson(Resource::class.java.classLoader.getResourceAsStream("render_config.json"))
    val dmmData = DmmReader.readMap(File(DMM_PATH))
    val dmm = Dmm(dmmData, dme)
    return DmmDrawer.drawMap(dmm, FilterMode.IGNORE, *IGNORE_TYPES)
}

private fun createSubImages(img: BufferedImage, zoomFolderPath: String, zoomFactor: Int, scaleFactor: Double) {
    val imageToCrop: BufferedImage

    if (scaleFactor != 1.0) {
        val scaleSize = (img.width * scaleFactor).toInt()
        val scaledImage = img.getScaledInstance(scaleSize, scaleSize, Image.SCALE_SMOOTH)
        imageToCrop = BufferedImage(scaleSize, scaleSize, BufferedImage.TYPE_INT_ARGB)

        val g = imageToCrop.createGraphics()
        g.drawImage(scaledImage, 0, 0, null)
        g.dispose()
    } else {
        imageToCrop = img
    }

    val imageSize = imageToCrop.width / zoomFactor
    for (x in 0 until zoomFactor) {
        for (y in 0 until zoomFactor) {
            val subImg = imageToCrop.getSubimage(x * imageSize, y * imageSize, imageSize, imageSize)
            if (!isBlankImage(subImg)) {
                val imgPath = String.format("%s/%s-%s.png", zoomFolderPath, y, x)
                ImageIO.write(subImg, "png", File(imgPath))
            }
        }
    }
}

private fun isBlankImage(img: BufferedImage): Boolean {
    val w = img.width
    val h = img.height
    val pixels = IntArray(w * h)

    val pg = PixelGrabber(img, 0, 0, w, h, pixels, 0, w)
    pg.grabPixels()

    var isBlank = true
    for (pixel in pixels) {
        if (pixel != 0) {
            isBlank = false
            break
        }
    }

    return isBlank
}
