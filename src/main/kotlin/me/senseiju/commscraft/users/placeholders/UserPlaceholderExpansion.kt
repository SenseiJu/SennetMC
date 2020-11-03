package me.senseiju.commscraft.users.placeholders

import me.senseiju.commscraft.BasePlaceholderExpansion
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.users.UserManager
import me.senseiju.commscraft.users.calculateMaxFishCapacity
import org.bukkit.entity.Player

class UserPlaceholderExpansion(private val plugin: CommsCraft, private val userManager: UserManager)
    : BasePlaceholderExpansion(plugin) {

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if (player == null) return ""

        if (params == "user_current_fish_capacity") return userManager.userMap[player.uniqueId]?.currentFishCaughtCapacity().toString()
        if (params == "user_max_fish_capacity") {
            return userManager.userMap[player.uniqueId]?.fishCapacityUpgrades?.let { calculateMaxFishCapacity(it).toString() }
        }

        return null
    }
}