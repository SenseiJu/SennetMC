package me.senseiju.sennetmc.scrap.commands

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Completion
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.sennetmc.PERMISSION_SCRAP_GIVE
import me.senseiju.sennetmc.equipment.fishing_net.NET_NBT_KEY
import me.senseiju.sennetmc.scrap.createScrapItem
import me.senseiju.sennetmc.scrap.getScrapAmountFromItem
import me.senseiju.sennetmc.scrap.isItemScrap
import me.senseiju.sennetmc.scrap.removeScrapAmountFromItem
import me.senseiju.sennetmc.utils.extensions.addItemOrDropNaturally
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@Command("Scrap")
class ScrapCommand : CommandBase() {

    @SubCommand("Give")
    @Permission(PERMISSION_SCRAP_GIVE)
    fun onGive(
        sender: CommandSender,
        @Completion("#players") targetPlayer: Player?,
        scrapAmount: Long?
    ) {
        if (targetPlayer == null) {
            sender.sendConfigMessage("CANNOT-FIND-TARGET")
            return
        }

        if (scrapAmount == null) {
            sender.sendConfigMessage("SCRAP-INVALID-AMOUNT")
            return
        }

        targetPlayer.inventory.addItemOrDropNaturally(createScrapItem(scrapAmount))
    }

    @SubCommand("Split")
    fun onSplit(sender: Player, scrapAmountToRemove: Long?) {
        if (scrapAmountToRemove == null || scrapAmountToRemove <= 0) {
            sender.sendConfigMessage("SCRAP-INVALID-AMOUNT")
            return
        }

        val item = sender.inventory.itemInMainHand
        if (!isItemScrap(item)) {
            sender.sendConfigMessage("SCRAP-INVALID-ITEM")
            return
        }

        if (scrapAmountToRemove >= getScrapAmountFromItem(item)) {
            sender.sendConfigMessage("SCRAP-TOO-MUCH-TO-SPLIT")
            return
        }

        sender.inventory.setItemInMainHand(removeScrapAmountFromItem(item, scrapAmountToRemove))
        sender.inventory.addItemOrDropNaturally(createScrapItem(scrapAmountToRemove))
        sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f)
    }
}