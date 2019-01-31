import static java.lang.Math.abs

if (!src.isType('/turf/simulated/wall'))
    return

int junction = 0

oRange(src, 1).eachForTypeOnly('/turf/simulated/wall') { W ->
    if (abs(src.x - W.x) - abs(src.y - W.y)) {
        if (src.mineral == W.mineral) {
            junction |= getDir(src, W)
        }
    }
}

src.icon_state = "${src.walltype}$junction"
