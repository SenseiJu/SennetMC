package me.senseiju.commscraft.npcs.types.merchant

import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.npcs.BaseNpc
import me.senseiju.commscraft.npcs.types.NpcType
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE = "eyJ0aW1lc3RhbXAiOjE1ODgwMjIwMTYzODMsInByb2ZpbGVJZCI6IjE5MjUyMWI0ZWZkYjQyNWM4OTMxZjAyYTg0OTZlMTFiIiwicHJvZmlsZU5hbWUiOiJTZXJpYWxpemFibGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU0OTY5ZDU1NDY0NmVmNWE5NGRlNDcyODBlYTY3ZTJkYzMwNmM3YTA5YTQwMjE5MDMzNDQyMGRhYTA2YzM2MDYifX19"
private const val SKIN_SIGNATURE = "feoHGYSz08NXIy5oUlhcThrahCagolldnojlpTqPYqFxPzS4CvmahCt3BApnz74LiM2Uql5eRmtDrP2fxag/0t15yz/xrJjWE8jPfvps1ltLub9P4rRB28bCQntSHV7N6GxN/fW1gXZ2vAaeMDqEzwkZMvxmObuy5nPCpPZUR0/Jht6ftW25gO8UctjwZhsbGRxD4zjv4+7GyeHcoRs9bmyqH7Qq+oDRyeIsaLyfNXXLTLUUQiDRLxtFZTCKTmMILYInJKVt6UoUEjTciptttEosM+JmznjrggGUs/VKGPU2igPKld7SZFJuUTRzsM3jPAH0SNc4e0BrH/kuNFbQv8EqoSRuKGUTQyleGrhItJKWtQqcM49AHNAMu9DAeWAljTwRhioQRjeJ7AIjOnmBkw2iHe07n+3JfXrYOMN94OqmSlbRa2w5mxQSWEDT47qQ67BD4bZHa2c0EFWmD1xqbNerfg6yhx9iX5dTUHcteuTzK93vEnF8bgtv1bG4sGBVSMXJUIlQ5cacsTJFwNE/UvMkvHpwifZzJapQUqlZVUjQvXdg+PEARf9uzwgEuut3JSyr1y70T4DIyzk/uUg2QDnKknaxfZtcxAXVITp5kWuLm+hn6naC8QRgMhvI218pE3ngSGvHbN6SbIJVEuu8m8MU3Hm5XPU6NMnsudgODao="

private val NPC_TYPE = NpcType.MERCHANT

class Merchant(plugin: CommsCraft) : BaseNpc {
    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun spawnNpc(location: Location) {
        val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, NPC_TYPE.npcName)
        npc.name = NPC_TYPE.npcName
        npc.isProtected = true
        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent("commscraft_merchant", SKIN_SIGNATURE, SKIN_TEXTURE)
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.GOLD_BLOCK))
        npc.spawn(location)
    }

    @EventHandler
    override fun onNpcRightClick(e: NPCRightClickEvent) {
        if (e.npc.name != NPC_TYPE.npcName) {
            return
        }

        showMerchantUpgradeGui(e.clicker)
    }
}