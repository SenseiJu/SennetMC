package me.senseiju.sennetmc.npcs.types.scrapper

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.createBasicNpc
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE = ""
private const val SKIN_SIGNATURE = ""

private val NPC_TYPE = NpcType.SCRAPPER

class Scrapper(plugin: SennetMC) : BaseNpc {

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile

    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)

        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)

        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.TRIPWIRE_HOOK))
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.OFF_HAND, ItemStack(Material.RED_DYE))

        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        val user = users[e.clicker.uniqueId] ?: return
        val fishCaughtMinimum = upgradesFile.config.getInt("scrapper-fish-caught-minimum", 50)

        if (user.totalFishCaught < fishCaughtMinimum) {
            e.clicker.sendConfigMessage(
                "SCRAPPER-NOT-ENOUGH-FISH-CAUGHT", false,
                PlaceholderSet("{scrapperName}", NPC_TYPE.npcName),
                PlaceholderSet("{fishAmount}", fishCaughtMinimum)
            )
            return
        }

        showScrapperGui(e.clicker)
    }

}