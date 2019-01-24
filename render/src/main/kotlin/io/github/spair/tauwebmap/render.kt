package io.github.spair.tauwebmap

import io.github.spair.byond.dme.parser.DmeParser
import io.github.spair.byond.dmm.Dmm
import io.github.spair.byond.dmm.drawer.DmmDrawer
import io.github.spair.byond.dmm.drawer.FilterMode
import io.github.spair.dmm.io.reader.DmmReader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.PixelGrabber
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

private const val REVISIONS = ".revisions"

private const val REPO_PATH = "tmp/repo"
private const val DME_PATH = "$REPO_PATH/taucetistation.dme"
private const val DMM_PATH = "$REPO_PATH/maps/z1.dmm"

private val LAYERS = arrayOf("tiles", "pipes", "power", "dispo")
private val TYPES_TO_RENDER = mapOf(
    "tiles" to arrayOf(), // All types except of IGNORE_TYPES.
    "pipes" to arrayOf("/obj/machinery/atmospherics/pipe"),
    "power" to arrayOf("/obj/structure/cable"),
    "dispo" to arrayOf("/obj/structure/disposalpipe")
)

private val IGNORE_TYPES = arrayOf("/turf/space", "/area", "/obj/effect/landmark")
private val COMPRESSION_ARGS = arrayOf("--ext=.png", "--force", "--strip", "--speed=1", "--nofs", "--posterize=2")

// All values calculated with assumption that map image size is 8160x8160
private val ZOOM_FACTORS = mapOf(3 to 8, 4 to 16, 5 to 32)
private val SCALE_FACTORS = mapOf(3 to 0.3, 4 to 0.6, 5 to 1.0)

private val FILES_TO_COMPRESS = ArrayList<String>(4200)
private val COMPRESS_FILE_COUNTER = AtomicInteger(0)

fun main() {
    File(REVISIONS).forEachLine { line ->
        val revision = line.split(" ")[1]
        ProcessBuilder("git", "checkout", revision).directory(File(REPO_PATH)).start().waitFor()
        renderRevision(revision)
    }

    COMPRESS_FILE_COUNTER.set(FILES_TO_COMPRESS.size)
    FILES_TO_COMPRESS.forEach { path ->
        GlobalScope.launch {
            ProcessBuilder("pngquant", *COMPRESSION_ARGS, path).start().waitFor()
            COMPRESS_FILE_COUNTER.decrementAndGet()
        }
    }

    println("Compressing files, this may take several minutes")
    while (COMPRESS_FILE_COUNTER.get() > 0) {
        print("\r  - remains: $COMPRESS_FILE_COUNTER")
    }
    println("\rImages generation completed!")
}

private fun renderRevision(revision: String) {
    println("Generating images for $revision:")
    LAYERS.forEach { layer ->
        print("  - $layer...")
        renderLayer("data/maps/$revision/$layer", TYPES_TO_RENDER[layer]!!)
        println(" OK!")
    }
}

private fun renderLayer(layerFolderPath: String, typesToUse: Array<String>) {
    val generatedImg = generateMapImage(typesToUse)

    ZOOM_FACTORS.forEach { zoom, zoomFactor ->
        val zoomFolder = File("$layerFolderPath/$zoom").apply { mkdirs() }
        createSubImages(generatedImg, zoomFolder.path, zoomFactor, SCALE_FACTORS[zoom]!!)
    }

    File(layerFolderPath).listFiles().forEach { zoomFolder ->
        zoomFolder.listFiles().forEach { imgFile ->
            FILES_TO_COMPRESS.add(imgFile.path)
        }
    }
}

internal class Resource
private fun generateMapImage(typesToUse: Array<String>): BufferedImage {
    val dme = DmeParser.parse(File(DME_PATH))
    dme.mergeWithJson(Resource::class.java.classLoader.getResourceAsStream("render_config.json"))
    val dmmData = DmmReader.readMap(File(DMM_PATH))
    val dmm = Dmm(dmmData, dme)

    return if (typesToUse.isEmpty()) {
        DmmDrawer.drawMap(dmm, FilterMode.IGNORE, *IGNORE_TYPES)
    } else {
        DmmDrawer.drawMap(dmm, FilterMode.INCLUDE, *typesToUse)
    }
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
                ImageIO.write(subImg, "png", File("$zoomFolderPath/$y-$x.png"))
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
