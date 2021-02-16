package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.utils.extensions.*
import me.senseiju.sennetmc.npcs.calculateNextUpgradeCost
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.updateUpgradeGuiItem
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.defaultScope
import me.senseiju.sennetmc.utils.secondsToTimeFormat
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

        gui.setItem(2, 3, chefSellGuiItem(chef, player))
        gui.setItem(2, 7, chefUpgradesGuiItem(chef, player))

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun chefSellGuiItem(chef: Chef, player: Player) : GuiItem {
    val lore = arrayListOf("")

    val runnable = chef.chefSellRunnables[player.uniqueId]
    if (runnable != null) {
        lore.add("&a&l** ACTIVE **")
        lore.add("")
        lore.add("&7Time remaining: &e${secondsToTimeFormat(runnable.timeToComplete)}")
        lore.add("&7Finished: ${runnable.finished.string}")
    } else {
        lore.add("&7Sell your fish to the chef who will resale")
        lore.add("&7for a higher price. You can come and collect")
        lore.add("&7your earnings later with a much higher value")
        lore.add("")
        lore.add("&c&lWARNING: &7Once you have given fish to the chef")
        lore.add("&7you will not be able to cancel the process and must")
        lore.add("&7wait the time")
    }

    return ItemBuilder.from(Material.FURNACE)
        .setName("&b&lSell fish".color())
        .setLore(lore.color())
        .asGuiItem {
            if (runnable == null) {
                createChefSellRunnable(chef, player)
                return@asGuiItem
            }

            if (runnable.finished) {
                if (econ != null) {
                    chef.chefSellRunnables.remove(player.uniqueId)

                    collectMoneyFromRunnable(runnable, player)
                }
            } else {
                player.sendConfigMessage(
                    "CHEF-ALREADY-RUNNING", false,
                    PlaceholderSet("{chefName}", npc_type.npcName))
            }

            player.closeInventory()
            return@asGuiItem
        }
}

private fun collectMoneyFromRunnable(chefSellRunnable: ChefSellRunnable, player: Player) {
    val user = users[player.uniqueId] ?: return
    val multiplier = upgradesFile.config.getDouble("chef-sell-base-multiplier", 6.0) + getSeasoningMultiplier(user)
    val sellPrice = (chefSellRunnable.initialSellPrice * multiplier).round()

    econ?.depositPlayer(player, sellPrice)

    player.sendTitle("&b&lCollected &e&l$${sellPrice}".color(), null, 20, 60, 20)
    player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
}

private fun createChefSellRunnable(chef: Chef, player: Player) {
    val user = users[player.uniqueId] ?: return

    if (user.currentFishCaughtCapacity <= 0) {
        player.sendConfigMessage("CHEF-NO-FISH", false,
            PlaceholderSet("{chefName}", npc_type.npcName))
        player.closeInventory()
        return
    }

    val timePerCapacity = upgradesFile.config.getLong("chef-sell-time-per-capacity", 40)
    val timeToComplete = (timePerCapacity - getServingSpeedUpgrade(user)) * user.currentFishCaughtCapacity
    val runnable = ChefSellRunnable(player.uniqueId, user.calculateSellPrice(), timeToComplete)

    user.clearCurrentFishCaught()

    chef.chefSellRunnables[player.uniqueId] = runnable
    chef.startChefSellRunnable(runnable)

    player.sendConfigMessage("CHEF-STARTED-RUNNING", false,
        PlaceholderSet("{chefName}", npc_type.npcName))
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
        val user = users[player.uniqueId] ?: return@launch
        val gui = defaultGuiTemplate(3, npc_type.npcName)

        gui.setCloseGuiAction {
            openChefGui(chef, player)
        }

        gui.setItem(2, 3, createSeasoningUpgradeGuiItem(gui, user))
        gui.setItem(2, 7, createServingSpeedUpgradeGuiItem(gui, user))

        scheduler.runTask(plugin, Runnable { gui.open(player) })
    }
}

private fun createSeasoningUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.SEASONING)
    val upgradeMax = upgradesFile.config.getInt("seasoning-upgrade-max", 20)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("seasoning-upgrade-starting-cost", 400.0),
        currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7The Chef will season the food before selling")
    lore.add("&7increasing the amount of money you will earn")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.SEAGRASS, "&b&lSeasoning", user, upgradeCost, upgradeMax, lore,
        Upgrade.SEASONING) { createSeasoningUpgradeGuiItem(gui, user) }
}

private fun createServingSpeedUpgradeGuiItem(gui: Gui, user: User) : GuiItem {
    val currentUpgrades = user.getUpgrade(Upgrade.SERVING_SPEED)
    val upgradeMax = upgradesFile.config.getInt("serving-speed-upgrade-max", 15)
    val upgradeCost = calculateNextUpgradeCost(
        upgradesFile.config.getDouble("serving-speed-starting-cost", 800.0),
        currentUpgrades)

    val lore = ArrayList<String>()
    lore.add("")
    lore.add("&7The Chef will serve the fish quicker decreasing")
    lore.add("&7the time it takes for you to be paid")
    lore.add("")
    lore.add("&7Cost: &e$$upgradeCost")
    lore.add("&7Current upgrades/Max upgrades: &e$currentUpgrades/$upgradeMax")

    return updateUpgradeGuiItem(gui, Material.POTION, "&b&lServing speed", user, upgradeCost, upgradeMax, lore,
        Upgrade.SERVING_SPEED) { createServingSpeedUpgradeGuiItem(gui, user) }
}

private fun getSeasoningMultiplier(user: User) : Double {
    return user.getUpgrade(Upgrade.SEASONING)
        .times(upgradesFile.config.getDouble("seasoning-upgrade-increment", 0.2))
}

private fun getServingSpeedUpgrade(user: User) : Long {
    return user.getUpgrade(Upgrade.SERVING_SPEED)
        .times(upgradesFile.config.getLong("serving-speed-upgrade-increment", 1))
}