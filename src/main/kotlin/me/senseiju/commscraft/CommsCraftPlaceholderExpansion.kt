package me.senseiju.commscraft

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.senseiju.commscraft.upgrades.Upgrade
import me.senseiju.commscraft.users.calculateMaxFishCapacity
import me.senseiju.commscraft.users.calculateSpeedboatSpeedMultiplier
import org.bukkit.entity.Player

class CommsCraftPlaceholderExpansion(private val plugin: CommsCraft) : PlaceholderExpansion() {

    private val userManager = plugin.userManager
    private val speedboatManager = plugin.speedboatManager

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

        val user = userManager.userMap[player.uniqueId] ?: return null
        return when (params) {
            "user_current_fish_capacity" ->
                user.currentFishCaughtCapacity.toString()
            "user_max_fish_capacity" ->
                calculateMaxFishCapacity(user.upgrades.getOrDefault(Upgrade.FISH_CAPACITY, 0)).toString()
            "user_speedboat_current_toggle" ->
                if (speedboatManager.playerSpeedboatToggle.getOrDefault(player.uniqueId, false)) "&a&lTrue" else "&c&lFalse"
            "user_speedboat_speed_multiplier" ->
                calculateSpeedboatSpeedMultiplier(user.upgrades.getOrDefault(Upgrade.SPEEDBOAT_SPEED, 0)).toString()
            "user_collectables_collected" ->
                user.collectables.size.toString()
            else -> null
        }
    }
}