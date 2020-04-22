package scripts.equal_more_e5c78eb95

import static java.lang.Math.abs
import io.github.spair.byond.dmm.TileItem

if (!src.isType('/obj/structure/window'))
    return

if (src.isType('/obj/structure/window/reinforced/shuttle'))
    return

if (!isFulltile(src)) {
    src.icon_state = src.basestate
    return
}

int junction = 0

if (src.anchored) {
    oRange(src, 1).eachForTypeOnly('/obj/structure/window') { W ->
        if (W.anchored && W.density && isFulltile(W) && W.can_merge) {
            if (abs(src.x - W.x) - abs(src.y - W.y)) {
                junction |= getDir(src, W)
            }
        }
    }
}

src.icon_state = "${src.basestate}$junction"

boolean isFulltile(TileItem item) {
    return (item.dir & (item.dir - 1))
}
