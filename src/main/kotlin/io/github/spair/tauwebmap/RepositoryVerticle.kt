package io.github.spair.tauwebmap

import io.vertx.core.AbstractVerticle
import java.io.File

class RepositoryVerticle : AbstractVerticle() {

    init {
        File(REPO_FOLDER).mkdir()
    }
}
