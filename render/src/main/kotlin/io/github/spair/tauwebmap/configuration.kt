package io.github.spair.tauwebmap

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import java.io.File
import java.lang.Exception

private const val CONFIG_FILE = "config.json"

private const val MORE_EQUAL_ENTRY = ">="
private const val MORE_ENTRY = ">"
private const val EQUAL_ENTRY = "=="
private const val LESS_ENTRY = "<"
private const val LESS_EQUAL_ENTRY = "<="

val RENDER_CONFIG = mutableMapOf<String, MutableList<String>>()
val SCRIPT_CONFIG = mutableMapOf<String, MutableList<File>>()

fun readConfigForRevisions(revisionList: List<String>) {
    val configJson = Json.parse(readResource(CONFIG_FILE)).asObject()

    configJson.entryForEach(MORE_EQUAL_ENTRY) { config ->
        for (revision in revisionList) {
            addToRenderConfig(revision, config)
            addToScriptConfig(revision, config)
            if (config.getRevision() == revision) {
                break
            }
        }
    }
    configJson.entryForEach(MORE_ENTRY) { config ->
        for (revision in revisionList) {
            if (config.getRevision() == revision) {
                break
            }
            addToRenderConfig(revision, config)
            addToScriptConfig(revision, config)
        }
    }
    configJson.entryForEach(EQUAL_ENTRY) { config ->
        for (revision in revisionList) {
            if (config.getRevision() == revision) {
                addToRenderConfig(revision, config)
                addToScriptConfig(revision, config)
            }
        }
    }
    configJson.entryForEach(LESS_ENTRY) { config ->
        for (revision in revisionList.reversed()) {
            if (config.getRevision() == revision) {
                break
            }
            addToRenderConfig(revision, config)
            addToScriptConfig(revision, config)
        }
    }
    configJson.entryForEach(LESS_EQUAL_ENTRY) { config ->
        for (revision in revisionList.reversed()) {
            addToRenderConfig(revision, config)
            addToScriptConfig(revision, config)
            if (config.getRevision() == revision) {
                break
            }
        }
    }
}

private fun addToRenderConfig(revision: String, config: JsonObject) {
    if (config.excluded(revision))
        return
    RENDER_CONFIG.getOrPut(revision) { mutableListOf() }.let { list ->
        config.getRender()?.forEach { renderFileName ->
            try {
                list.add(renderFileName.asString())
            } catch (e: Exception) {
                println("exception with $renderFileName")
                throw e
            }
        }
    }
}

private fun addToScriptConfig(revision: String, config: JsonObject) {
    if (config.excluded(revision))
        return
    SCRIPT_CONFIG.getOrPut(revision) { mutableListOf() }.let { list ->
        config.getScript()?.forEach { scriptFileName ->
            try {
                list.add(resourceFile(scriptFileName.asString()))
            } catch (e: Exception) {
                println("exception with $scriptFileName")
                throw e
            }
        }
    }
}

private fun JsonObject.entryForEach(entryName: String, action: (JsonObject) -> (Unit)) {
    get(entryName)?.asArray()?.forEach { action(it.asObject()) }
}

private fun JsonObject.getRender() = get("render")?.asArray()
private fun JsonObject.getScript() = get("script")?.asArray()
private fun JsonObject.getRevision() = get("revision").asString()

private fun JsonObject.excluded(revision: String): Boolean {
    val exclude = get("exclude")?.asArray()
    if (exclude != null) {
        for (excl in exclude) {
            if (revision == excl.asString()) {
                return true
            }
        }
    }
    return false
}

internal class Resource
fun readResource(path: String) = Resource::class.java.classLoader.getResource(path).readText()
fun resourceFile(path: String) = File(Resource::class.java.classLoader.getResource(path).file)
