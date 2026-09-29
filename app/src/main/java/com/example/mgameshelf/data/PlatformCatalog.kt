package com.example.mgameshelf.data


object PlatformCatalog {
    val SONY = listOf("PS1", "PS2", "PS3", "PSP", "PS VITA", "PS4", "PS5")
    val NINTENDO = listOf(
        "NES", "SNES", "GameBoy", "GameBoy Color", "Nintendo 64", "Nintendo Gamecube", "GameBoy Advance",
        "Nintendo Wii", "Nintendo DS", "Wii U", "Nintendo 3DS", "Nintendo Switch", "Nintendo Switch 2"
    )
    val MICROSOFT = listOf("Xbox", "Xbox 360", "Xbox One", "Xbox Series S", "Xbox Series X")
    val OTRAS = listOf("PC")

    /** Mapa ordenado: encabezado del grupo -> plataformas de ese grupo. */
    val AGRUPADO: LinkedHashMap<String, List<String>> = linkedMapOf(
        "Sony" to SONY,
        "Nintendo" to NINTENDO,
        "Microsoft" to MICROSOFT,
        "Otras" to OTRAS
    )
}