package me.senseiju.sennetmc.npcs.types.scrapper

import me.senseiju.sennetmc.npcs.createBasicNpc
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE = ""
private const val SKIN_SIGNATURE = ""

private val NPC_TYPE = NpcType.SCRAPPER

class Scrapper : BaseNpc {

    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)

        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)

        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.TRIPWIRE_HOOK))
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.OFF_HAND, ItemStack(Material.RED_DYE))

        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {

    }

}