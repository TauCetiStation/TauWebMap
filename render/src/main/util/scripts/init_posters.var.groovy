if (!src.isType('/obj/structure/sign/poster'))
    return

def serialNumber = rand(1, src.official ? legitPostersAmount : contrabandPostersAmount)

if (src.official) {
    src.icon_state = "poster${serialNumber}_legit"
} else {
    src.icon_state = "poster$serialNumber"
}
