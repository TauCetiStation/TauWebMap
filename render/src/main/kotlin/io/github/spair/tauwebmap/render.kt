package io.github.spair.tauwebmap

import io.github.spair.byond.dme.Dme
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

private val LAYERS = arrayOf("tiles", "pipes", "power", "dispo")
private val TYPES_TO_RENDER = mapOf(
    "tiles" to arrayOf(), // All types except of IGNORE_TYPES.
    "pipes" to arrayOf("/obj/machinery/atmospherics/pipe", "/obj/machinery/atmospherics/components/unary/vent_pump", "/obj/machinery/atmospherics/components/unary/vent_scrubber"),
    "power" to arrayOf("/obj/structure/cable"),
    "dispo" to arrayOf("/obj/structure/disposalpipe")
)

private val IGNORE_TYPES = arrayOf("/turf/space", "/area", "/obj/effect/landmark")

// All values calculated with assumption that map image size is 8160x8160
private val ZOOM_FACTORS = mapOf(3 to 8, 4 to 16, 5 to 32)
private val SCALE_FACTORS = mapOf(3 to 0.3, 4 to 0.6, 5 to 1.0)

private var CURRENT_REVISION = ""

fun main(args: Array<String>) {
    val revisionList = mutableListOf<String>()

    File(REVISIONS).forEachLine { line ->
        if (line.isNotBlank()) {
            revisionList.add(line.split(" ")[1])
        }
    }

    readConfigForRevisions(revisionList, args.isNotEmpty() && args[0] == "-d")

    revisionList.forEach { revision ->
        ProcessBuilder("git", "checkout", revision).directory(File(REPO_PATH)).start().waitFor()
        renderRevision(revision)
    }
}

private fun renderRevision(revision: String) {
    println("Generating images for $revision:")
    CURRENT_REVISION = revision
    LAYERS.forEach { layer ->
        print("  - $layer...")
        renderLayer("data/maps/$revision/$layer", TYPES_TO_RENDER.getValue(layer))
        println(" OK!")
    }
}

private fun renderLayer(layerFolderPath: String, typesToUse: Array<String>) {
    val generatedImg = generateMapImage(typesToUse)

    ZOOM_FACTORS.forEach { zoom, zoomFactor ->
        val zoomFolder = File("$layerFolderPath/$zoom").apply { mkdirs() }
        createSubImages(generatedImg, zoomFolder.path, zoomFactor, SCALE_FACTORS.getValue(zoom))
    }
}

private fun generateMapImage(typesToUse: Array<String>): BufferedImage {
    val dme = DmeParser.parse(File(DME_PATH)).apply { mergeDmeWithConfigJsons(this) }
    val dmmData = DmmReader.readMap(File(DMM_PATH))
    val dmm = Dmm(dmmData, dme)

    return if (typesToUse.isEmpty()) {
        DmmDrawer.drawMap(dmm, SCRIPT_CONFIG[CURRENT_REVISION], FilterMode.IGNORE, *IGNORE_TYPES)
    } else {
        DmmDrawer.drawMap(dmm, FilterMode.INCLUDE, *typesToUse)
    }
}

private fun mergeDmeWithConfigJsons(dme: Dme) {
    RENDER_CONFIG[CURRENT_REVISION]?.forEach { filePath ->
        dme.mergeWithJson(File(filePath))
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
