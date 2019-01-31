if (src.isType('/obj/item/weapon/wirecutters')) {
    if (src.random_color) {
        def paramColor = src.param_color
        if (!paramColor) {
            paramColor = pick(['yellow', 'red', 'orange'])
        }
        src.icon_state = "cutters_$paramColor"
    }
} else if (src.isType('/obj/item/weapon/screwdriver')) {
    if (src.random_color) {
        def paramColor = src.param_color
        if (!paramColor) {
            paramColor = pick(['red', 'blue', 'purple', 'brown', 'green', 'cyan', 'yellow' ])
        }
        src.icon_state = "screwdriver_$paramColor"
    }
    src.pixel_y = rand(-6, 6)
    src.pixel_x = rand(-4, 4)
}
