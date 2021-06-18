package me.senseiju.sennetmc.npcs.types.chef

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.createBasicNpc
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

private const val SKIN_TEXTURE =
    "ewogICJ0aW1lc3RhbXAiIDogMTYxMjk4NTYyMTk4OCwKICAicHJvZmlsZUlkIiA6ICIxNzhmMTJkYWMzNTQ0ZjRhYjExNzkyZDc1MDkzY2JmYyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzaWxlbnRkZXRydWN0aW9uIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2IzNDUxOWMyZjE4NTcyNDI1ZTdjMGMxZWQ4MDY2Yzg1MWExZDUzZjllNTU0MWQ0ZjczMGFhZjQ0NWRkYmZmMTciCiAgICB9CiAgfQp9"
private const val SKIN_SIGNATURE =
    "vZoQrN0Rvka5gcoi1w3e+auW4etuRbZi+dpFVos86v+hOCma1GO/db9Kh6k8NtTSZhnrP5i5LAdi0Jy2i+WmBirrscxHIBMEJniU6oC906//DSmeyzGyqf92ti2H0JoqUtwpbDb8NLxVO1+fUUS903XmGa2cS1pUcyXbJvVQ9/ahdVBCxfh1T/UBI2rs0HJcDlGdvADu0xxyn/kY/gqYLyktcWueqA56sUx/1aEpWlxn+iMojb6WWaKVIiKXjwbO/sUzmm1PyV6Ud1OTd+9Fq5UiMF2XLId5LSxB0Y8bzlOdAjCDWI4nbpoE6w8yVHsvBLkxAZZfIpa+Nc2Os61Mw//Knj/UYFTuA1KLWWThTxmfarA2ZSltSk7chDbnT7tGGUN3lo4sx4Hetapwwf1Unl4Y1V4TMR9uBjaOCSZN38A/p89LKwJHfH8HL2+MYsqFjjcs9HavVAJjTiRDGA1oWoxqJto41StS7tl3hC15XGMjgsTrAQvliKn8t0Qf3o8F/q3bX3jxaSQF2hKYQHTJH2WfTBT00Xb0vCfRJ3lGp5SL6hB/zLM86yGfPBPgppTy41W3n85pjJrf9GW4RSiUCHamDCT4plzT+GEUy1Uw15JSZBcZUpnpnHp7KbOJQ+4RIErvjqFr83Onf7wXn1+YYtV/0WtAmpKsQSdORq83YXw="

private val NPC_TYPE = NpcType.CHEF

class Chef(private val plugin: SennetMC) : BaseNpc {

    val chefSellRunnables = HashMap<UUID, ChefSellRunnable>()

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile
    private val chefSellRunnablesFile = RawDataFile(plugin, "chefSellRunnables.json")

    init {
        loadChefSellRunnables()
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
            e.clicker.sendConfigMessage(
                "NPC-MINIMUM-FISH-REQUIRED", false,
                PlaceholderSet("{npcName}", NPC_TYPE.npcName),
                PlaceholderSet("{fishAmount}", fishCaughtMinimum)
            )
            return
        }

        openChefGui(this, e.clicker)
    }

    fun saveChefSellRunnables() {
        chefSellRunnablesFile.write(Json.encodeToString(chefSellRunnables.map { it.value.toJson() }))
    }

    private fun loadChefSellRunnables() {
        val json: List<String>

        try {
            json = Json.decodeFromString(chefSellRunnablesFile.read())
        } catch (ex: Exception) {
            println("Failed to parse 'chefSellRunnables.json'. Could be empty")
            return
        }

        json.forEach {
            val chefSellRunnable = SerializableChefSellRunnable.fromJson(it)

            chefSellRunnables[chefSellRunnable.uuid] = chefSellRunnable

            chefSellRunnable.start(plugin)
        }
    }
}