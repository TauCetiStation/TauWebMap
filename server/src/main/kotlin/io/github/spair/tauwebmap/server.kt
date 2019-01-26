package io.github.spair.tauwebmap

import io.netty.handler.codec.http.HttpHeaderNames
import org.http4k.core.Response
import org.http4k.core.ContentType
import org.http4k.core.maxAge
import org.http4k.core.Method.GET
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.filter.CachingFilters.Response.MaxAge as CacheMaxAge
import org.http4k.routing.ResourceLoader.Companion.Classpath
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes
import org.http4k.routing.static
import org.http4k.server.Netty
import org.http4k.server.asServer
import java.io.File
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.Duration
import java.util.Date

val YEAR_DURATION = Duration.ofDays(365)!!
val MOTH_DURATION = Duration.ofDays(30)!!
val WEEK_DURATION = Duration.ofDays(7)!!

fun main() {
    routes(
        "/" bind GET to {
            Response(OK).contentType(ContentType.TEXT_HTML).body(Classpath("/webroot").load("index.html")!!.openStream())
        },
        "/static" bind static(Classpath("/webroot/static")).withFilter(CacheMaxAge(Clock.systemUTC(), YEAR_DURATION)),
        "/tiles/{revision}/{layer}/{zoom}/{y}/{x}" bind GET to { req ->
            val revision = req.path("revision")
            val layer = req.path("layer")
            val zoom = req.path("zoom")
            val y = req.path("y")
            val x = req.path("x")

            val tilePath = "data/maps/$revision/$layer/$zoom/$y-$x.png"
            val tileImg = File(tilePath)

            if (tileImg.exists()) {
                Response(OK).maxAge(MOTH_DURATION).contentType("image/png").body(tileImg.inputStream())
            } else {
                Response(NO_CONTENT).maxAge(WEEK_DURATION)
            }
        }
    ).asServer(Netty(3000)).start().also {
        println("${SimpleDateFormat("HH:mm:ss dd.MM.yyyy").format(Date())} | TauWebMap started")
    }
}

fun Response.contentType(type: String) = header("${HttpHeaderNames.CONTENT_TYPE}", type)
fun Response.contentType(type: ContentType) = contentType(type.value)
