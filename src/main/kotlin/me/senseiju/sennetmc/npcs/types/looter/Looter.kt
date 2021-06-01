package me.senseiju.sennetmc.npcs.types.looter

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.createBasicNpc
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.types.looter.commands.LooterCommand
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE =
    "ewogICJ0aW1lc3RhbXAiIDogMTYxMjEyNTI5OTE2OSwKICAicHJvZmlsZUlkIiA6ICIzOWEzOTMzZWE4MjU0OGU3ODQwNzQ1YzBjNGY3MjU2ZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJkZW1pbmVjcmFmdGVybG9sIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2M1YzBiNzcxMzc4NzkzMDQ1OTI0Y2FlODgzYzYzNGFjODdlMDY1NDFkZjBiMjc5MjgwNWQwYTQxOWM1Y2FmYzgiCiAgICB9CiAgfQp9"
private const val SKIN_SIGNATURE =
    "lCqjPtxEH73Gmw9n7irDvx1R+t8FyFDHXQ4Im7PngqowERru6pIITSYCpq5CaPsV4Peaf+LyGUHdrS6zlFkal0AbgkzP86BR16V6UBFdI61aDmtabiKmKdcIf5VQR1dOLzDO6Wk7q+UTIBChL0jsR14K/nFVOPa1iEM4brg3VHd6242+89L97OvyStZpzduzRy6vlvjLqG+Ynkcs9b8yxeis/HyzH6WtWh1aBCBZWojoEgYCxv2MBQEaHhNNu42mQWn5OTMG+KE6w1agaJEcUikwQtRRD8dLzK2nhEr1ml5WdpU/ERvZn/981//mEhjAyU+M6lnQOGKstiVZRLG6mIYdAwpi/BOj/3/Sx0TlzUIkpl5bPgywN7Oa5o6hnRTPg+BsBWigh1YO9OamXcIrsZ+dJAYDVQHrOwqGcG+mBrD2kH0HK7KEf4M1LfbnPLr843jDiW7xYhbVhnjOGMgCTkwKU0N/O6OoojjX1XqG2OSnCWHbq+jjmqifRxNbPSOx6njTzn20l4TUySEyKUMDBQVUDjL6jlHdE/NQogeN+MnKPtR7IEEiJuq4KfCIPltVYFjX/8NmHjPIpSgUuJWp8Z30MZsYXPKsdcvFa1gbVRWekYWEDNztMN1aFkzEA738XmeiNG8IoYt/VqkXnnmIvsHRFX1D2XEr4M1lrCCfA4A="

private val NPC_TYPE = NpcType.LOOTER

class Looter(plugin: SennetMC) : BaseNpc {

    init {
        plugin.commandManager.register(LooterCommand())
    }

    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)
        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.CHEST))
        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        showLooterGui(e.clicker)
    }
}