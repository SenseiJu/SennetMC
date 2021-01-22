package me.senseiju.commscraft.settings

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.int
import me.senseiju.commscraft.settings.commands.SettingsCommand
import java.util.*

private const val SELECT_QUERY = "SELECT * FROM `settings` WHERE `uuid`=?;"

class SettingsManager(private val plugin: CommsCraft) : BaseManager {

    init {
        registerCommands(plugin.commandManager)
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {
        cm.register(SettingsCommand(plugin))
    }

    override fun registerEvents() {
    }

    override fun reload() {
    }

    suspend fun fetchSettings(uuid: UUID) : EnumMap<Setting, Boolean> {
        val set = plugin.database.asyncQuery(SELECT_QUERY, uuid.toString())
        return if (set.next()) Setting.mapFromSet(set)
        else EnumMap<Setting, Boolean>(Setting::class.java)
    }

    fun updateSettings(uuid: UUID, settingsMap: EnumMap<Setting, Boolean>) {
        val settings = Setting.values().map { (settingsMap.getOrDefault(it, it.default)).int }.toTypedArray()

        plugin.database.updateQuery(Setting.buildUpdateDatabaseQuery(), uuid.toString(), *settings, *settings)
    }
}