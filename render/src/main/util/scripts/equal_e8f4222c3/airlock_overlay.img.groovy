if (!src.isType('/obj/machinery/door/airlock'))
    return sprite

def filling_overlay

if (src.glass == 1) {
    filling_overlay = getCachedDmi(src.overlays_file).getSprite("glass_closed")
} else {
    filling_overlay = getCachedDmi(src.icon).getSprite('fill_closed')
}

if (filling_overlay) {
	sprite.graphics.with {
		drawImage(filling_overlay, 0, 0, null)
		dispose()
	}
}

return sprite
