package scripts.equal_more_0bc341bb0

if (!src.isType('/obj/structure/table'))
    return

int dirSum = 0

for (direction in [1, 2, 4, 8, 5, 6, 9, 10]) {
    def skipSum = false

    for (W in dmm.getTile(src.x, src.y).tileItems) {
        if (W.isType('/obj/structure/window')) {
            if (W.dir == direction) {
                skipSum = true
                break
            }
        }
    }

    int inv_direction
    switch (direction) {
        case 1:
            inv_direction = 2
            break
        case 2:
            inv_direction = 1
            break
        case 4:
            inv_direction = 8
            break
        case 8:
            inv_direction = 4
            break
        case 5:
            inv_direction = 10
            break
        case 6:
            inv_direction = 9
            break
        case 9:
            inv_direction = 6
            break
        case 10:
            inv_direction = 5
            break
    }

    for (W in getStep(src, direction)) {
        if (W.isType('/obj/structure/window')) {
            if (W.dir == inv_direction) {
                skipSum = true
                break
            }
        }
    }

    if (!skipSum) {
        def T = locate('/obj/structure/table', getStep(src, direction))
        if (T && T.canconnect && src.canconnect) {
            if (direction < 5) {
                dirSum += direction
            } else {
                if (direction == 5)
                    dirSum += 16
                else if (direction == 6)
                    dirSum += 32
                else if (direction == 8)
                    dirSum += 8
                else if (direction == 10)
                    dirSum += 64
                else if (direction == 9)
                    dirSum += 128
            }
        }
    }
}

def tableType = 0

if (dirSum % 16 in [1, 2, 4, 8]) {
    tableType = 1
    dirSum %= 16
}

if (dirSum % 16 in [3, 12]) {
    tableType = 2
    if (dirSum % 16 == 3)
        dirSum = 2
    else if (dirSum % 16 == 12)
        dirSum = 4
}

if (dirSum % 16 in [5, 6, 9, 10]) {
    def T = locate('/obj/structure/table', getStep(src, dirSum % 16))
    if (T && T.canconnect && src.canconnect)
        tableType = 3
    else
        tableType = 2
    dirSum %= 16
}

if (dirSum % 16 in [13, 14, 7, 11]) {
    tableType = 5
    switch (dirSum % 16) {
        case 7:
            if (dirSum == 23) {
                tableType = 6
                dirSum = 8
            } else if (dirSum == 39) {
                dirSum = 4
                tableType = 6
            } else if (dirSum == 55 || dirSum == 119 || dirSum == 247 || dirSum == 183) {
                dirSum = 4
                tableType = 3
            } else {
                dirSum = 4
            }
            break
        case 11:
            if (dirSum == 75) {
                dirSum = 5
                tableType = 6
            } else if (dirSum == 139) {
                dirSum = 9
                tableType = 6
            } else if (dirSum == 203 || dirSum == 219 || dirSum == 251 || dirSum == 235) {
                dirSum = 8
                tableType = 3
            } else
                dirSum = 8
            break
        case 13:
            if (dirSum == 29) {
                dirSum = 10
                tableType = 6
            } else if (dirSum == 141) {
                dirSum = 6
                tableType = 6
            } else if (dirSum == 189 || dirSum == 221 || dirSum == 253 || dirSum == 157) {
                dirSum = 1
                tableType = 3
            } else
                dirSum = 1
            break
        case 14:
            if (14)
                if (dirSum == 46) {
                    dirSum = 1
                    tableType = 6
                } else if (dirSum == 78) {
                    dirSum = 2
                    tableType = 6
                } else if (dirSum == 110 || dirSum == 254 || dirSum == 238 || dirSum == 126) {
                    dirSum = 2
                    tableType = 3
                } else
                    dirSum = 2
            break
    }
}

if (dirSum % 16 == 15) {
    tableType = 4
}

switch (tableType) {
    case 0: break
    case 1:
        src.icon_state = "${src.icon_state}_1tileendtable"
        break
    case 2:
        src.icon_state = "${src.icon_state}_1tilethick"
        break
    case 3:
        src.icon_state = "${src.icon_state}_dir"
        break
    case 4:
        src.icon_state = "${src.icon_state}_middle"
        break
    case 5:
        src.icon_state = "${src.icon_state}_dir2"
        break
    case 6:
        src.icon_state = "${src.icon_state}_dir3"
        break
}

if (dirSum in [1, 2, 4, 8, 5, 6, 9, 10]) {
    src.dir = dirSum
} else {
    src.dir = 2
}
