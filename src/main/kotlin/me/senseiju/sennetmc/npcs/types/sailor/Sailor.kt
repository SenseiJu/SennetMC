package me.senseiju.sennetmc.npcs.types.sailor

import me.senseiju.sennetmc.PERMISSION_SPEEDBOAT_USE
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

private const val SKIN_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYxMDM2NzI5MjkxMSwKICAicHJvZmlsZUlkIiA6ICI3MmNiMDYyMWU1MTA0MDdjOWRlMDA1OTRmNjAxNTIyZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNb3M5OTAiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBmYTRkMWY0M2RhOThhZmU4ZWUyZTYwNzkyYjNlY2NjMWEwNjAyYTY1OTllMGQyOWM2NjBkY2E1YmIxZGRhMSIsCiAgICAgICJtZXRhZGF0YSIgOiB7CiAgICAgICAgIm1vZGVsIiA6ICJzbGltIgogICAgICB9CiAgICB9CiAgfQp9"
private const val SKIN_SIGNATURE = "qAYPRiEr/2yugTN0eP5gsUywcKr4PDrGe0JAIGgk2d0dsHrJSvhXr3MmimEO4pg+tSz1146zOdYQlAwcSdo+DlVzxxf6MaX9tVqNLC3hQZUdyAPWT+fNXyY89ADS4895lIJZ1i81HP+R7Yt23M6ndMXFjbz66sJUWs0Uwsc/7Naby3VpDYaEcDK/S7Zx2B34JP1DH1/hSxTqxPPrK3fuU+bqGEdORDyNK92koDvmPYGnKnLvQlN1Uzr6if48ZWEUurKnFjsx5zJ2o99SiWObUzcM6SIXzToSJYSZNj9zSggczHJAXVArdsd5OMh0CTmA7nzYlY6yLhozv0teb3ONi5ciw58RQg/uayc9nWK/SICpxhsuB/mKFUNrWrkRjHdQYEJwg5Hy4RwZ2w/Ju0YITdJbZQXTm2OSCS6nzJ3oh6H77lrIhiB+1e1xGw2h2eGYVow2m3oqOuHdlUNdrdlus9Gur3Vkawkv8wfG+7sbdTPSXYyluvzLum+sTyerFmyrkSnbs8OJEDDXLca5UKCbv7c4ivmB1Z5ca8kPqstc6ztwM77LoWwv2kXeiDC9Iy2XLSOx33ubqlY8Yf2dx0RMxOChayppopwvI+NEWg0w9CLPXngqvEYjO8zucJP/+FsWARNBWQCW3T/LwfBa+ILeqyurNsVzjRtPGDeznbe9cw4="

private val NPC_TYPE = NpcType.SAILOR

class Sailor : BaseNpc {

    override fun spawnNpc(location: Location) {
        val npc = createBasicNpc(NPC_TYPE)
        npc.getOrAddTrait(SkinTrait::class.java).setSkinPersistent(NPC_TYPE.name, SKIN_SIGNATURE, SKIN_TEXTURE)
        npc.getOrAddTrait(Equipment::class.java).set(Equipment.EquipmentSlot.HAND, ItemStack(Material.OAK_BOAT))
        npc.spawn(location)
    }

    override fun onNpcRightClick(e: NPCRightClickEvent) {
        if (!e.clicker.hasPermission(PERMISSION_SPEEDBOAT_USE)) {
            e.clicker.sendConfigMessage("SAILOR-REQUIRES-RANK", false,
                PlaceholderSet("{sailorName}", NPC_TYPE.npcName))
            return
        }

        showSailorGui(e.clicker)
    }
}