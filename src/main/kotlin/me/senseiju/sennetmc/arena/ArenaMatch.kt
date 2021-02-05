package me.senseiju.sennetmc.arena

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class ArenaMatch(val player1: ArenaPlayer, val player2: ArenaPlayer) : BukkitRunnable() {

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
            player1.refundInventoryWithWinnings(player2.wager)
            player2.refundInventory()
        } else {
            player2.refundInventoryWithWinnings(player1.wager)
            player1.refundInventory()
        }

        player1.teleportToPreviousLocation()
        player2.teleportToPreviousLocation()
    }

    fun getOpposingPlayer(player: Player) : Player {
        return if (player1.uuid == player.uniqueId) {
            player2.player
        } else {
            player1.player
        }
    }

    fun start(startingLocation1: Location, startingLocation2: Location) {
        handlePlayerStart(player1, startingLocation1)
        handlePlayerStart(player2, startingLocation2)
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
        player.inventory.setItem(0, ItemStack(Material.IRON_SWORD))
    }

    override fun run() {
    }
}