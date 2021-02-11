package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.*
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.ObjectSet
import me.senseiju.sennetmc.utils.defaultScope
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

private val npc_type = NpcType.CHEF

private val plugin = JavaPlugin.getPlugin(SennetMC::class.java)
private val scheduler = plugin.server.scheduler
private val upgradesFile = plugin.upgradesManager.upgradesFile
private val econ = plugin.server.servicesManager.getRegistration(Economy::class.java)?.provider
private val users = plugin.userManager.userMap

fun openChefGui(chef: Chef, player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, npc_type.npcName)

        gui.setItem(2, 7, chefUpgradesGuiItem(chef, player))
        gui.setItem(2, 3, chefSellGuiItem(chef, player))

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun chefSellGuiItem(chef: Chef, player: Player) : GuiItem {
    val lore = arrayListOf("")

    val runnable = chef.chefSellRunnables[player.uniqueId]
    if (runnable != null) {
        lore.add("&a&l** ACTIVE **")
        lore.add("")
        lore.add("&7Claimable: ${runnable.claimable.string}")
    } else {
        lore.add("&7Sell your fish to the chef who will resale")
        lore.add("&7for a higher price. You can come and collect")
        lore.add("&7your earnings later with a much higher value")
    }

    return ItemBuilder.from(Material.FURNACE)
        .setName("&b&lSell fish".color())
        .setLore(lore.color())
        .asGuiItem {
            if (runnable == null) {
                createChefSellRunnable(chef, player)
                return@asGuiItem
            }

            if (runnable.claimable) {
                if (econ != null) {
                    chef.chefSellRunnables.remove(player.uniqueId)

                    collectMoneyFromRunnable(runnable, player)
                }
            } else {
                player.sendConfigMessage(
                    "CHEF-ALREADY-RUNNING", false,
                    ObjectSet("{chefName}", npc_type.npcName))
            }

            player.closeInventory()
            return@asGuiItem
        }
}

private fun collectMoneyFromRunnable(chefSellRunnable: ChefSellRunnable, player: Player) {
    val multiplier = upgradesFile.config.getDouble("chef-sell-base-multiplier", 6.0)
    val sellPrice = (chefSellRunnable.initialSellPrice * multiplier).round()

    econ?.depositPlayer(player, sellPrice)

    player.sendTitle("&b&lCollected &e&l$${sellPrice}".color(), null, 20, 60, 20)
    player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
}

private fun createChefSellRunnable(chef: Chef, player: Player) {
    val user = users[player.uniqueId] ?: return
    val timePerCapacity = upgradesFile.config.getInt("chef-sell-time-per-capacity", 40)
    val timeToComplete = (timePerCapacity - getServingSpeedUpgrade(user)) * user.currentFishCaughtCapacity
    val runnable = ChefSellRunnable(player.uniqueId, timeToComplete, user.calculateSellPrice())

    user.clearCurrentFishCaught()

    chef.chefSellRunnables[player.uniqueId] = runnable
    chef.startChefSellRunnable(runnable)

    player.sendConfigMessage("CHEF-STARTED-RUNNING", false,
        ObjectSet("{chefName}", npc_type.npcName))
    player.closeInventory()
}

private fun chefUpgradesGuiItem(chef: Chef, player: Player) : GuiItem {
    return ItemBuilder.from(Material.CHEST)
        .setName("&b&lChef upgrades".color())
        .asGuiItem {
            openChefUpgradesGui(chef, player)
        }
}

private fun openChefUpgradesGui(chef: Chef, player: Player) {
    defaultScope.launch {
        val gui = defaultGuiTemplate(3, npc_type.npcName)

        gui.setCloseGuiAction {
            openChefGui(chef, player)
        }

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun getSeasoningMultiplier(user: User) : Double {
    return user.getUpgrade(Upgrade.SEASONING)
        .times(upgradesFile.config.getDouble("seasoning-upgrade-increment", 0.2))
}

private fun getServingSpeedUpgrade(user: User) : Int {
    return user.getUpgrade(Upgrade.SERVING_SPEED)
        .times(upgradesFile.config.getInt("serving-speed-upgrade-increment", 1))
}