package me.senseiju.commscraft.npcs.types.fishmonger

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.npcs.BaseNpc
import me.senseiju.commscraft.npcs.createBasicNpc
import me.senseiju.commscraft.npcs.types.NpcType
import me.senseiju.commscraft.utils.ObjectSet
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import net.milkbowl.vault.economy.Economy
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYwOTMwNDIxOTQwMywKICAicHJvZmlsZUlkIiA6ICJkMGI4MjE1OThmMTE0NzI1ODBmNmNiZTliOGUxYmU3MCIsCiAgInByb2ZpbGVOYW1lIiA6ICJqYmFydHl5IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQzMGMzOGU1ZjQ2MDRjZmJmZTlhMzZhMTVlYWQzMjNiZjBmNTMzOTFmMzk2NDBmYWIzMGVhYmQ3OGI1ODk5MzciCiAgICB9CiAgfQp9"
private const val SKIN_SIGNATURE = "WfIR1SfvLWiiah5JwzO5J8QkPdru+JC5Fto7gD6XKvNnQpKIe9YpnPlQizDedjiR+rms+9OyIT3psko4cgOtkHl1u9UZhGTm/0C3m1rkN4/7LmibYQbFs4SydIQ9ZF3569q/07+znWrWCgEnu8fjnKgZcEFiffrQKrMVTyZJ5C8miBZGMD8HnUqWq7LhpiqVr7sD3EprOpvG6PxRzM/xNFrkVT6aup5qdGiGhtX0t/+xKpLc6Ta0ctwI7dhENBmdSRQLz7mK+p3enBH0JATqV2EC5/y7TTnvTEhr0WsNTvUT6K2G9I6rt4s/He2nPuWfz67vw3eGh4zGxXm5bNArYtojFhwHBadLhUemhvqLOwURzo1nOawRKU9B2H7GO+3xbx61/mo+xRjRfu9tkUR9mdBaqcCBiO/nV15TqmRN7ngS+S91lJKnx+kt/l4ePuqhnbqsMRlI6GE3lxsfeP3W+dQwBBc1kWZ4Di41IdVe+AdM/aG2KWNEWVLU0dVA2OgBwDqhBsomgT2/6ejdbnW4tzxuhP88lvuehQWVjdqzgHt3BNoCyt19OI8NBiUOuL1DOKGqOjITWHwY/BtxmCL24+LszZMB8vq5s8NKgDuW0s34lhys92m3adF+VAw7RvoQPAM0Ndn7TiTLQ/pWk9VQS54jAHT3yM1nyVB+mwhKDig="

private val NPC_TYPE = NpcType.FISHMONGER

class Fishmonger(private val plugin: CommsCraft) : BaseNpc {
    private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider

    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)
        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.FISHING_ROD))
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.OFF_HAND, ItemStack(Material.TROPICAL_FISH_BUCKET))
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.BOOTS, ItemStack(Material.LEATHER_BOOTS))
        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        if (e.npc.name != NPC_TYPE.npcName) {
            return
        }

        val user = plugin.userManager.userMap[e.clicker.uniqueId] ?: return

        var totalSellPrice = 0.0
        for ((fishType, fishCaughtData) in user.fishCaught) {
            val sellPrice = fishType.selectRandomSellPrice() * fishCaughtData.current

            totalSellPrice += "%.2f".format(sellPrice).toDouble()

            fishCaughtData.current = 0
        }

        econ?.depositPlayer(e.clicker, totalSellPrice)

        if (totalSellPrice > 0) {
            e.clicker.sendTitle("&b&lSold for $${totalSellPrice}".color(), null, 20, 60, 20)
            e.clicker.playSound(e.clicker.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        } else {
            e.clicker.sendConfigMessage("FISHMONGER-NO-FISH-TO-SELL", false,
                    ObjectSet("{fishmongerName}", e.npc.name))
        }
    }
}