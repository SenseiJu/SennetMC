package me.senseiju.sennetmc.events.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.events.event.fishrace.FishRace
import org.bukkit.entity.Player

@Command("Events")
class EventsCommand(private val plugin: SennetMC) : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        FishRace(plugin, plugin.eventsManager)
    }
}