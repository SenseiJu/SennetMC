package me.senseiju.sennetmc.scrap.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_SCRAP_GIVE
import me.senseiju.sennetmc.scrap.*
import me.senseiju.sennetmc.utils.extensions.addItemOrDropNaturally
import me.senseiju.sennetmc.utils.extensions.isNullOrAir
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ThreadLocalRandom

@Command("Scrap")
class ScrapCommand : CommandBase() {
    private val random = SplittableRandom()

    @SubCommand("Give")
    @Permission(PERMISSION_SCRAP_GIVE)
    fun give(
        sender: CommandSender,
        @Completion("#players") targetPlayer: Player?,
        scrapAmount: String?
    ) {
        if (targetPlayer == null) {
            sender.sendConfigMessage("CANNOT-FIND-TARGET")
            return
        }

        if (scrapAmount == null) {
            sender.sendConfigMessage("SCRAP-INVALID-AMOUNT")
            return
        }

        val scrap = parseScrapAmount(scrapAmount)
        if (scrap == null) {
            sender.sendConfigMessage("SCRAP-INVALID-AMOUNT")
            return
        }

        targetPlayer.inventory.addScrap(scrap, true)
    }

    @SubCommand("Split")
    fun split(sender: Player, scrapAmountToRemove: Long?) {
        if (scrapAmountToRemove == null || scrapAmountToRemove <= 0) {
            sender.sendConfigMessage("SCRAP-INVALID-AMOUNT")
            return
        }

        val item = sender.inventory.itemInMainHand
        if (item.isNullOrAir() || !isItemScrap(item)) {
            sender.sendConfigMessage("SCRAP-INVALID-ITEM")
            return
        }

        if (scrapAmountToRemove >= getScrapAmountFromItem(item)) {
            sender.sendConfigMessage("SCRAP-TOO-MUCH-TO-SPLIT")
            return
        }

        sender.inventory.setItemInMainHand(removeScrapAmountFromItem(item, scrapAmountToRemove))
        sender.inventory.addScrap(scrapAmountToRemove, true)
        sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
    }

    private fun parseScrapAmount(scrapAmount: String): Long? {
        val possibleAmounts = scrapAmount.split("-")
        if (possibleAmounts.size == 1) {
            return possibleAmounts[0].toLongOrNull()
        }
        if (possibleAmounts.size > 2) {
            return null
        }

        val firstAmount = possibleAmounts[0].toLongOrNull() ?: return null
        val secondAmount = possibleAmounts[1].toLongOrNull() ?: return null
        if (firstAmount >= secondAmount) {
            return null
        }

        return random.nextLong(firstAmount, secondAmount + 1)
    }
}