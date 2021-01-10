package me.senseiju.commscraft

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.senseiju.commscraft.users.calculateMaxFishCapacity
import me.senseiju.commscraft.users.calculateSpeedboatSpeedMultiplier
import org.bukkit.entity.Player

class CommsCraftPlaceholderExpansion(private val plugin: CommsCraft) : PlaceholderExpansion() {

    private val userManager = plugin.userManager
    private val speedboatManager = plugin.speedboatManager
    private val collectablesManager = plugin.collectablesManager

    init {
        register()
    }

    override fun canRegister(): Boolean {
        return true
    }

    override fun persist(): Boolean {
        return true
    }

    override fun getIdentifier(): String {
        return "commscraft"
    }

    override fun getAuthor(): String {
        return plugin.description.authors.toString()
    }

    override fun getVersion(): String {
        return plugin.description.version
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null) return ""

        return when (params) {
            "user_current_fish_capacity" ->
                userManager.userMap[player.uniqueId]?.currentFishCaughtCapacity().toString()
            "user_max_fish_capacity" ->
                userManager.userMap[player.uniqueId]?.fishCapacityUpgrades?.let { calculateMaxFishCapacity(it).toString() }
            "user_speedboat_current_toggle" ->
                if (speedboatManager.playerSpeedboatToggle.getOrDefault(player.uniqueId, false)) "&a&lTrue" else "&c&lFalse"
            "user_speedboat_speed_multiplier" ->
                userManager.userMap[player.uniqueId]?.speedboatUpgrades?.let { calculateSpeedboatSpeedMultiplier(it).toString() }
            "user_collectables_collected" ->
                collectablesManager.collectablesCollected.getOrDefault(player.uniqueId, 0).toString()
            else -> null
        }
    }
}