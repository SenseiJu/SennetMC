package me.senseiju.commscraft

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

open class BasePlaceholderExpansion(private val plugin: CommsCraft) : PlaceholderExpansion() {
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
        return null
    }
}