package me.senseiju.sennetmc.npcs.types.scrapper

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.utils.PlaceholderSet
import me.senseiju.sennetmc.utils.datastorage.RawDataFile
import me.senseiju.sennetmc.utils.extensions.sendConfigMessage
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*
import me.senseiju.sennetmc.equipment.Equipment as SEquipment

private const val SKIN_TEXTURE = ""
private const val SKIN_SIGNATURE = ""

private val NPC_TYPE = NpcType.SCRAPPER

class Scrapper(private val plugin: SennetMC) : BaseNpc {

    val crafting = HashMap<UUID, EnumMap<SEquipment, CraftableEquipment>>()

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile
    private val craftingFile = RawDataFile(plugin, "scrapperCrafting.json")

    init {
        loadCrafting()
    }

    override fun spawnNpc(location: Location) {
        val npc = NPC_TYPE.createBasicNpc()

        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)

        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.TRIPWIRE_HOOK))
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.OFF_HAND, ItemStack(Material.RED_DYE))

        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        val user = users[e.clicker.uniqueId] ?: return
        val fishCaughtMinimum = upgradesFile.getInt("scrapper-fish-caught-minimum", 50)

        if (user.totalFishCaught < fishCaughtMinimum) {
            e.clicker.sendConfigMessage(
                "NPC-MINIMUM-FISH-REQUIRED",
                false,
                PlaceholderSet("{npcName}", NPC_TYPE.npcName),
                PlaceholderSet("{fishAmount}", fishCaughtMinimum)
            )
            return
        }

        showScrapperGui(this, e.clicker)
    }

    fun saveCrafting() {
        craftingFile.write(Json.encodeToString(crafting.map { it.value.map { craftable -> craftable.value.toJson() } }.flatten()))
    }

    private fun loadCrafting() {
        val json: List<String>

        try {
            json = Json.decodeFromString(craftingFile.read())
        } catch (ex: Exception) {
            println("Failed to parse 'scrapperCrafting.json'. Could be empty")
            return
        }

        json.forEach {
            val craftable = CraftableEquipment.fromJson(it)

            crafting.computeIfAbsent(craftable.uuid) { EnumMap(SEquipment::class.java) }[craftable.equipment] = craftable

            craftable.start(plugin)
        }
    }

}