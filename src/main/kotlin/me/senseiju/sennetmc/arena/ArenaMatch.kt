package me.senseiju.sennetmc.arena

import com.destroystokyo.paper.Title
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.extensions.sendConfigMessage
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ArenaMatch(val player1: ArenaPlayer, val player2: ArenaPlayer) : BukkitRunnable() {

    var pvpEnabled = false
        private set

    private var countdown = 5

    fun involvesPlayer(uuid: UUID) : Boolean = player1.uuid == uuid || player2.uuid == uuid

    fun refundWagers() {
        player1.refundInventoryAndWager()
        player2.refundInventoryAndWager()
    }

    fun cancelMatch() {
        player1.teleportToPreviousLocation()
        player1.refundInventoryAndWager()

        player2.teleportToPreviousLocation()
        player2.refundInventoryAndWager()
    }

    fun handleWinner(winner: Player) {
        if (player1.uuid == winner.uniqueId) {
            doWinner(player1, player2)
        } else {
            doWinner(player2, player1)
        }
    }

    private fun doWinner(winner: ArenaPlayer, loser: ArenaPlayer) {
        winner.refundInventoryWithWinnings(loser.wager)
        loser.refundInventory()

        winner.player.sendConfigMessage("ARENA-WIN")
        loser.player.sendConfigMessage("ARENA-LOSE")

        winner.teleportToPreviousLocation()
        loser.teleportToPreviousLocation()
    }

    fun getOpposingPlayer(player: Player) : Player {
        return if (player1.uuid == player.uniqueId) {
            player2.player
        } else {
            player1.player
        }
    }

    fun start(plugin: SennetMC, startingLocation1: Location, startingLocation2: Location) {
        handlePlayerStart(player1, startingLocation1)
        handlePlayerStart(player2, startingLocation2)

        runTaskTimer(plugin, 20L, 20L)
    }

    private fun handlePlayerStart(arenaPlayer: ArenaPlayer, location: Location) {
        arenaPlayer.previousInventory = arenaPlayer.player.inventory.contents
        arenaPlayer.previousLocation = arenaPlayer.player.location.clone()
        arenaPlayer.player.inventory.clear()
        
        equipGear(arenaPlayer.player)

        arenaPlayer.player.teleport(location)
    }

    private fun equipGear(player: Player) {
        player.inventory.chestplate = ItemStack(Material.IRON_CHESTPLATE)
        player.inventory.leggings = ItemStack(Material.IRON_LEGGINGS)
        player.inventory.boots = ItemStack(Material.IRON_BOOTS)
        player.inventory.setItem(0, ItemStack(Material.STONE_SWORD))
    }

    override fun run() {
        if (countdown <= 0) {
            pvpEnabled = true

            cancel()
            return
        }

        sendCountdownMessage(player1.player)
        sendCountdownMessage(player2.player)

        countdown--
    }

    private fun sendCountdownMessage(player: Player) {
        player.sendTitle("$countdown", null, 0, 28, 0)
        player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_SNARE, 1.0f, 1.0f)
    }
}