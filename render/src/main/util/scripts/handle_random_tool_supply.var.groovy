if (src.isType('/obj/random/tools/tech_supply')) {
    def icon = randomToolsIcons[pick(randomToolsNames)]
    src.icon = "'$icon.icon'"
    src.icon_state = pick(icon.states)
}
