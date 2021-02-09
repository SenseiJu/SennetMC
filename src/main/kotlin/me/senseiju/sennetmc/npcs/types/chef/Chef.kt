package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.datastorage.DataFile
import me.senseiju.sennetmc.datastorage.RawDataFile
import me.senseiju.sennetmc.extensions.sendConfigMessage
import me.senseiju.sennetmc.npcs.createBasicNpc
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.types.designer.commands.CosmeticsCommand
import me.senseiju.sennetmc.utils.ObjectSet
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

private const val SKIN_TEXTURE = ""
private const val SKIN_SIGNATURE = ""

private val NPC_TYPE = NpcType.CHEF

class Chef(plugin: SennetMC) : BaseNpc {

    val chefSellRunnables = HashMap<UUID, ChefSellRunnable>()

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile
    private val chefSellRunnablesFile = RawDataFile(plugin, "chefSellRunnables.json")

    init {
        loadChefSellRunnables()

        plugin.commandManager.register(CosmeticsCommand())
    }

    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)

        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.FURNACE))

        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        val user = users[e.clicker.uniqueId] ?: return
        val fishCaughtMinimum = upgradesFile.config.getInt("chef-fish-caught-minimum", 50)

        if (user.totalFishCaught < fishCaughtMinimum) {
            e.clicker.sendConfigMessage("CHEF-NOT-ENOUGH-FISH-CAUGHT", false,
                ObjectSet("{chefName}", NPC_TYPE.name),
                ObjectSet("{fishAmount}", fishCaughtMinimum))
            return
        }

        openChefGui(e.clicker)
    }

    fun saveChefSellRunnables() {
        chefSellRunnablesFile.write(Json.encodeToString(chefSellRunnables.map { it.value.toJson() }))
    }

    private fun loadChefSellRunnables() {
        val json = Json.decodeFromString<List<String>>(chefSellRunnablesFile.read())

        json.forEach {
            val chefSellRunnable = ChefSellRunnableData.fromJson(it)

            chefSellRunnables[chefSellRunnable.uuid] = chefSellRunnable
        }
    }
}