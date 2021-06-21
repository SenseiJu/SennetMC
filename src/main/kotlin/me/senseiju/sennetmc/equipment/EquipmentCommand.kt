package me.senseiju.sennetmc.equipment

import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_EQUIPMENT_GIVE
import me.senseiju.sennetmc.equipment.fishing_net.createFishingNetItem
import me.senseiju.sennetmc.utils.extensions.addItemOrDropNaturally
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import org.bukkit.entity.Player

@Command("equipment")
class EquipmentCommand : CommandBase() {

    @SubCommand("give")
    @Permission(PERMISSION_EQUIPMENT_GIVE)
    fun onGive(
        sender: Player,
        @Completion("#players") player: Player?,
        @Completion("#enum") equipment: Equipment?,
        @Optional amount: Int?
    ) {
        if (player == null) {
            sender.sendConfigMessage("CANNOT-FIND-TARGET")
            return
        }

        if (equipment == null) {
            sender.sendConfigMessage("EQUIPMENT-CANNOT-FIND-EQUIPMENT")
            return
        }

        if (amount != null && amount <= 0) {
            sender.sendConfigMessage("INVALID-AMOUNT")
            return
        }

        when (equipment) {
            Equipment.FISHING_NET -> player.inventory.addItemOrDropNaturally(createFishingNetItem(amount ?: 1))
        }
    }
}