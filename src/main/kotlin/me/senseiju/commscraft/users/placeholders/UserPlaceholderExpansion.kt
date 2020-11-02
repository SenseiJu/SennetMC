package me.senseiju.commscraft.users.placeholders

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.UserManager
import org.bukkit.entity.Player

class UserPlaceholderExpansion(private val plugin: CommsCraft, private val userManager: UserManager) : PlaceholderExpansion() {
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
        return plugin.description.authors.toString();
    }

    override fun getVersion(): String {
        return plugin.description.version;
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null) return ""

        if (params == "user_current_fish_capacity") return userManager.userMap[player.uniqueId]?.currentFishCaughtCapacity().toString()
        if (params == "user_max_fish_capacity") return userManager.userMap[player.uniqueId]?.maxFishCapacity.toString()

        return null
    }
}