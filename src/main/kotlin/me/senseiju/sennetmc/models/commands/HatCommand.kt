package me.senseiju.sennetmc.models.commands

import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.SubCommand
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.models.isPassengerBackpackModel
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot

@Command("Hat")
class HatCommand(private val plugin: SennetMC) : CommandBase() {

    @Default
    fun onCommand(player: Player) {
        player.inventory.setItem(EquipmentSlot.HEAD, player.inventory.itemInMainHand)
    }

    @SubCommand("a")
    fun onASubCommand(player: Player) {
        val target = player.getTargetEntity(4)

        if (target == null || target.type != EntityType.ARMOR_STAND) return

        val stand = target as ArmorStand

        stand.equipment?.helmet = player.inventory.itemInMainHand
    }

    @SubCommand("b")
    fun onBSubCommand(player: Player) {
        player.passengers.forEach {
            if (isPassengerBackpackModel(it)) {
                it as ArmorStand
                it.equipment?.helmet = player.inventory.itemInMainHand
                return
            }
        }
    }
}