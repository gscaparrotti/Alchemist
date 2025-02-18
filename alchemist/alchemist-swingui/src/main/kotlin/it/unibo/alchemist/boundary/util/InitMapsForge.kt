package it.unibo.alchemist.boundary.util

import java.io.File
import org.mapsforge.map.layer.download.tilesource.OpenStreetMapMapnik

object InitMapsForge {
    @JvmStatic
    fun initAgent() {
        with(OpenStreetMapMapnik.INSTANCE) {
            val folderName = (""".*\""" + File.separator + "(.*)").toRegex()
            val experiment = folderName.matchEntire(System.getProperty("user.dir"))
                ?.groups?.get(1)?.value ?: "unkown experiment"
            userAgent = "$experiment via Alchemist Simulator (alchemistsimulator.github.io)"
            referer = "alchemistsimulator.github.io"
        }
    }
}
