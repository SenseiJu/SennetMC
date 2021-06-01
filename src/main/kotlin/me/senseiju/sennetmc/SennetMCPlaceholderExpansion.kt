package me.senseiju.sennetmc

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.calculateMaxFishCapacity
import me.senseiju.sennetmc.users.calculateSpeedboatSpeedMultiplier
import org.bukkit.entity.Player

class SennetMCPlaceholderExpansion(private val plugin: SennetMC) : PlaceholderExpansion() {

    private val userManager = plugin.userManager
    private val speedboatManager = plugin.speedboatManager
    private val arenaManager = plugin.arenaManager

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
        return "sennetmc"
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
                calculateMaxFishCapacity(user.getUpgrade(Upgrade.FISH_CAPACITY)).toString()
            "user_speedboat_current_toggle" ->
                if (speedboatManager.playerSpeedboatToggle.getOrDefault(
                        player.uniqueId,
                        false
                    )
                ) "&a&lTrue" else "&c&lFalse"
            "user_speedboat_speed_multiplier" ->
                calculateSpeedboatSpeedMultiplier(user.getUpgrade(Upgrade.SPEEDBOAT_SPEED)).toString()
            "user_collectables_collected" ->
                user.collectables.size.toString()
            "arena_player1" ->
                arenaManager.currentMatch?.player1?.player?.name ?: "No one"
            "arena_player2" ->
                arenaManager.currentMatch?.player2?.player?.name ?: "No one"
            else -> null
        }
    }
}