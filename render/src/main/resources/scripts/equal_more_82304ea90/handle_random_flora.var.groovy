package scripts.from_82304ea90

if (!src.isType('/obj/structure/flora/plant/random'))
    return

def plant = env.getItem(pick(env.getItem('/obj/structure/flora/pottedplant').getSubtypes()))
src.icon = "'$plant.icon'"
src.icon_state = plant.icon_state
