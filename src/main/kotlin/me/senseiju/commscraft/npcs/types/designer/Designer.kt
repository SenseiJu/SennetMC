package me.senseiju.commscraft.npcs.types.designer

import me.senseiju.commscraft.npcs.BaseNpc
import me.senseiju.commscraft.npcs.createBasicNpc
import me.senseiju.commscraft.npcs.types.NpcType
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE = ""
private const val SKIN_SIGNATURE = ""

private val NPC_TYPE = NpcType.DESIGNER

class Designer : BaseNpc {
    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)

        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.LEATHER_HELMET))

        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        showDesignerGui(e.clicker)
    }
}