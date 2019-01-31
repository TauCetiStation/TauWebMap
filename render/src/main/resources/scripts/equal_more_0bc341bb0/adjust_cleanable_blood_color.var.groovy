package scripts.from_0bc341bb0

if (!src.isType('/obj/effect/decal/cleanable/blood'))
    return

src.color = env.getItem(src.basedatum).color
