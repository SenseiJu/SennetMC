package me.senseiju.sennetmc.equipment

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.equipment.fishing_net.FishingNetListener
import me.senseiju.sennetmc.utils.datastorage.DataFile

class EquipmentManager(plugin: SennetMC) : BaseManager() {
    val equipmentFile = DataFile(plugin, "equipment.yml", true)

    init {
        registerCommands(plugin.commandManager)
        registerEvents(plugin)
    }

    override fun registerCommands(cm: CommandManager) {
        cm.parameterHandler.register(Equipment::class.java, ParameterResolver { argument ->
            try {
                return@ParameterResolver TypeResult(Equipment.valueOf(argument.toString().uppercase()), argument)
            } catch (ex: IllegalArgumentException) {
                return@ParameterResolver TypeResult(argument)
            }
        })

        cm.register(EquipmentCommand())
    }

    override fun registerEvents(plugin: SennetMC) {
        plugin.registerEvents(FishingNetListener(plugin, this))
    }

    override fun reload() {
        equipmentFile.reload()
    }
}