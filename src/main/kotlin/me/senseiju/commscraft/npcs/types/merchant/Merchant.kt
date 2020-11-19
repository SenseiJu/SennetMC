package me.senseiju.commscraft.npcs.types.merchant

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.npcs.BaseNpc
import me.senseiju.commscraft.npcs.types.NpcType
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.milkbowl.vault.economy.Economy
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler

class Merchant(private val plugin: CommsCraft) : BaseNpc {
    private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider

    private val npcName = "&6&lMerchant Manny".color()
    private val dataFile = NpcType.MERCHANT.dataFile

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun spawnNpc(location: Location) {
        val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, npcName)
        npc.name = npcName
        npc.isProtected = true
        npc.spawn(location)
    }

    @EventHandler
    override fun onNpcRightClick(e: NPCRightClickEvent) {
        if (e.npc.name != npcName) {
            return
        }

        showMerchantUpgradeGui(e.clicker)
    }
}