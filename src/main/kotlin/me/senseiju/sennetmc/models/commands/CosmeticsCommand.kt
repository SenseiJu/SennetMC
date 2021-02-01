package me.senseiju.sennetmc.models.commands

import me.mattstudios.mf.annotations.Alias
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.base.CommandBase
import me.senseiju.sennetmc.PERMISSION_MODELS_COSMETICS
import me.senseiju.sennetmc.npcs.types.designer.showDesignerGui
import org.bukkit.entity.Player

@Command("Cosmetics")
@Alias("Cosmetic")
class CosmeticsCommand : CommandBase() {

    @Default
    @Permission(PERMISSION_MODELS_COSMETICS)
    fun onCommand(player: Player) {
        showDesignerGui(player)
    }
}