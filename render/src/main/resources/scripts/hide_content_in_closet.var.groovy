if (!src.isType('/obj/structure/closet'))
    return

dmm.getTile(src.x, src.y).tileItems.eachForTypeOnly('/obj/item') { I ->
    if (!I.density && !I.anchored) {
        I.alpha = 0
    }
}
