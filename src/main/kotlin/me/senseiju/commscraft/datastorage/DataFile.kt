package me.senseiju.commscraft.datastorage

import me.senseiju.commscraft.CommsCraft
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class DataFile(private val plugin: CommsCraft, private val path: String, private val hasDefault: Boolean = false) {
    private var file: File = File("${plugin.dataFolder}/${path}")

    lateinit var config: YamlConfiguration

    init {
        load()
    }

    fun load() {
        if (!file.exists()) {
            plugin.dataFolder.mkdirs()
            if (hasDefault) {
                plugin.saveResource(path, false)
            } else {
                file.createNewFile()
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
    }

    fun save() = config.save(file)
}