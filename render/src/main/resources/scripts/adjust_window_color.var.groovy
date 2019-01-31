if (!src.isType('/obj/structure/window') && !src.isType('/obj/machinery/door/window'))
    return

def currentArea = dmm.getTile(src.x, src.y).getArea()
def colored = false

areaColorsLists.each { areaColorList ->
    if (colored) return
    areaColorList.each { areaList, color ->
        if (colored) return
        if (currentArea.type in areaList) {
            src.color = color
            colored = true
        }
    }
}
