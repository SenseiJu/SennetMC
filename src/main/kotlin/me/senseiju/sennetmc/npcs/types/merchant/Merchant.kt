package me.senseiju.sennetmc.npcs.types.merchant

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.types.merchant.commands.SellCommand
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import me.senseiju.sentils.extensions.primitives.color
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import net.milkbowl.vault.economy.Economy
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE =
    "eyJ0aW1lc3RhbXAiOjE1ODgwMjIwMTYzODMsInByb2ZpbGVJZCI6IjE5MjUyMWI0ZWZkYjQyNWM4OTMxZjAyYTg0OTZlMTFiIiwicHJvZmlsZU5hbWUiOiJTZXJpYWxpemFibGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzU0OTY5ZDU1NDY0NmVmNWE5NGRlNDcyODBlYTY3ZTJkYzMwNmM3YTA5YTQwMjE5MDMzNDQyMGRhYTA2YzM2MDYifX19"
private const val SKIN_SIGNATURE =
    "feoHGYSz08NXIy5oUlhcThrahCagolldnojlpTqPYqFxPzS4CvmahCt3BApnz74LiM2Uql5eRmtDrP2fxag/0t15yz/xrJjWE8jPfvps1ltLub9P4rRB28bCQntSHV7N6GxN/fW1gXZ2vAaeMDqEzwkZMvxmObuy5nPCpPZUR0/Jht6ftW25gO8UctjwZhsbGRxD4zjv4+7GyeHcoRs9bmyqH7Qq+oDRyeIsaLyfNXXLTLUUQiDRLxtFZTCKTmMILYInJKVt6UoUEjTciptttEosM+JmznjrggGUs/VKGPU2igPKld7SZFJuUTRzsM3jPAH0SNc4e0BrH/kuNFbQv8EqoSRuKGUTQyleGrhItJKWtQqcM49AHNAMu9DAeWAljTwRhioQRjeJ7AIjOnmBkw2iHe07n+3JfXrYOMN94OqmSlbRa2w5mxQSWEDT47qQ67BD4bZHa2c0EFWmD1xqbNerfg6yhx9iX5dTUHcteuTzK93vEnF8bgtv1bG4sGBVSMXJUIlQ5cacsTJFwNE/UvMkvHpwifZzJapQUqlZVUjQvXdg+PEARf9uzwgEuut3JSyr1y70T4DIyzk/uUg2QDnKknaxfZtcxAXVITp5kWuLm+hn6naC8QRgMhvI218pE3ngSGvHbN6SbIJVEuu8m8MU3Hm5XPU6NMnsudgODao="

private val NPC_TYPE = NpcType.MERCHANT

class Merchant(private val plugin: SennetMC) : BaseNpc {

    init {
        plugin.commandManager.register(SellCommand(this))
    }

    private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
    private val upgradesFile = plugin.upgradesManager.upgradesFile

    override fun spawnNpc(location: Location) {
        val npc = NPC_TYPE.createBasicNpc()
        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.EMERALD))
        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        sellPlayersFish(e.clicker)
    }

    fun sellPlayersFish(player: Player) {
        val user = plugin.userManager.userMap[player.uniqueId] ?: return
        val multiplier = 1 + getNegotiateMultiplier(user)
        val totalSellPrice = user.calculateSellPrice(multiplier)

        if (totalSellPrice > 0) {
            econ?.depositPlayer(player, totalSellPrice)

            user.clearCurrentFishCaught()

            player.sendTitle(
                "&b&lSold for &e&l$${totalSellPrice}".color(),
                "&6x$multiplier multiplier".color(),
                20,
                60,
                20
            )
            player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
        } else {
            player.sendConfigMessage(
                "MERCHANT-NO-FISH-TO-SELL", false,
                PlaceholderSet("{merchantName}", NPC_TYPE.npcName)
            )
        }
    }

    private fun getNegotiateMultiplier(user: User): Double {
        return user.getUpgrade(Upgrade.NEGOTIATE)
            .times(upgradesFile.getDouble("negotiate-upgrade-increment", 0.05))
    }
}