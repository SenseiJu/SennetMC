package me.senseiju.sennetmc.npcs.types.scrapper

import kotlinx.coroutines.launch
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.extensions.defaultGuiTemplate
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val npcType = NpcType.SCRAPPER
private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler

fun showScrapperGui(player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, npcType.npcName)



        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}