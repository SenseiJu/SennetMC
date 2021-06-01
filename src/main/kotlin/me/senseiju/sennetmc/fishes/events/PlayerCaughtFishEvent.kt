package me.senseiju.sennetmc.fishes.events

import me.senseiju.sennetmc.fishes.FishType
import me.senseiju.sennetmc.users.User
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerCaughtFishEvent(val player: Player, val user: User, val fishType: FishType) : Cancellable, Event() {
    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }

    private var cancelled = false

    override fun getHandlers(): HandlerList = handlerList

    override fun isCancelled(): Boolean = cancelled

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }


}