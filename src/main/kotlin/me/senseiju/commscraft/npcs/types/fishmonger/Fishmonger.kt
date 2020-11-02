package me.senseiju.commscraft.npcs.types.fishmonger

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.fishes.FishType
import me.senseiju.commscraft.npcs.types.BaseNpc
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.utils.ObjectSet
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.npc.skin.Skin
import net.citizensnpcs.npc.skin.SkinnableEntity
import net.milkbowl.vault.economy.Economy
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import java.lang.Math.round

class Fishmonger(private val plugin: CommsCraft) : BaseNpc {
    private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider

    private val npcName = "&3&lFishmonger Freddy".color()
    private val dataFile = NpcType.FISHMONGER.dataFile

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

        val user = plugin.userManager.userMap[e.clicker.uniqueId] ?: return

        var totalSellPrice = 0.0
        for ((fishType, amount) in user.currentFishCaught) {
            val sellPrice = fishType.selectRandomSellPrice() * amount
            totalSellPrice += "%.2f".format(sellPrice).toDouble()

            user.currentFishCaught[fishType] = 0
        }
        econ?.depositPlayer(e.clicker, totalSellPrice)

        if (totalSellPrice > 0) {
            e.clicker.sendTitle("&b&lSold for $${totalSellPrice}".color(), null, 20, 60, 20)
            e.clicker.playSound(e.clicker.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        } else {
            e.clicker.sendConfigMessage("FISHMONGER-NO-FISH-TO-SELL", false,
                    ObjectSet("{fishmongerName}", e.npc.entity.name))
        }
    }
}