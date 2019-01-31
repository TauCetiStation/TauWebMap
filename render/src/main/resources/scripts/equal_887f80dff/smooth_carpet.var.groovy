import io.github.spair.byond.dmm.TileItem

import static io.github.spair.byond.dmi.SpriteDir.*

if (!src.isType('/turf/simulated/floor/carpet'))
    return

if (src.icon_state == "carpetsymbol")
    return 

int connectDir = 0

for (direction in [1, 2, 4, 8]) {
    def loc = getStep(src, direction).getLoc()
    if (isCarpetFloor(loc)) {
        connectDir |= direction
    }
}

int diagonalConnect = 0

// Northeast
if (connectDir & NORTH.dirValue && connectDir & EAST.dirValue) {
    def loc = getStep(src, NORTHEAST.dirValue).getLoc()
    if (isCarpetFloor(loc)) {
        diagonalConnect |= 1
    }
}

// Southeast
if (connectDir & SOUTH.dirValue && connectDir & EAST.dirValue) {
    def loc = getStep(src, SOUTHEAST.dirValue).getLoc()
    if (isCarpetFloor(loc)) {
        diagonalConnect |= 2
    }
}

// Northwest
if (connectDir & NORTH.dirValue && connectDir & WEST.dirValue) {
    def loc = getStep(src, NORTHWEST.dirValue).getLoc()
    if (isCarpetFloor(loc)) {
        diagonalConnect |= 4
    }
}

// Southwest
if (connectDir & SOUTH.dirValue && connectDir & WEST.dirValue) {
    def loc = getStep(src, SOUTHWEST.dirValue).getLoc()
    if (isCarpetFloor(loc)) {
        diagonalConnect |= 8
    }
}

src.icon_state = "carpet$connectDir-$diagonalConnect"

boolean isCarpetFloor(TileItem item) {
    return item.floor_type?.contains('/obj/item/stack/tile/carpet')
}