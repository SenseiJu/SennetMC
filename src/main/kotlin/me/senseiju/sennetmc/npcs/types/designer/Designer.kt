package me.senseiju.sennetmc.npcs.types.designer

import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.npcs.types.BaseNpc
import me.senseiju.sennetmc.npcs.createBasicNpc
import me.senseiju.sennetmc.npcs.types.NpcType
import me.senseiju.sennetmc.npcs.types.designer.commands.CosmeticsCommand
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.trait.SkinTrait
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

private const val SKIN_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYxMjEyNTIxMTEzNSwKICAicHJvZmlsZUlkIiA6ICI0ZWQ4MjMzNzFhMmU0YmI3YTVlYWJmY2ZmZGE4NDk1NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJGaXJlYnlyZDg4IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg1ZTVlNmZiZWI3ZDhiZWZiNjVhNzdlM2YxN2UwOGQxZDQ4MzEyODIxOWNkY2MwMjkzNjc3NmI2YjliZDgwMDYiCiAgICB9CiAgfQp9"
private const val SKIN_SIGNATURE = "kK1hmM7mb3kKsFgI0TLQO05jl5mHLMci94mA4oZkX/HIAwGolLib4wI7QKCDjNFA0jiU5oqi+IEh3BvaWEGPSkk786mkR37HQn+uW0SkqyNRZavDV+3Wy6PbqDe0HcxAzy//ZA7+lo5jHchQfIMwsqy5dkSYs81kmzqywDypSLN8vxqKaLoRmKDzwOBjPZZKOc8qVfclIU8d7raCIS/BbRAYORObaCER9Fre8hCDkjKhqSrATkeghDEP4/t/jS7PmdnIUyjla1YNcQwANXc9VUf9PW0nfga84YNdP9LqB3MuIxk13b7RnnF2R8i5oq/qnoyaaf9KP+i19NaspUZW9VA97+4dFhBbPXgr6UTnFWcjsKztQwjlfmXq5dKNatPFI4JvcbZr2oS0mfcNwrOvASRiKA8VfaUzzfPBFRgMI//0JWvEKjecyvMTUyvjaWeizu2rIicxTfbLn0VvMBh7EIbzNt99CfoXm+SnslEMK6V391LSRVgnQwSOJ7K6255LjGpyS1xNR5VLNsVDgA80wIm/gW6gCp4IvpLTi9UBW/ABpYf7vbZ/mDLGEHbQmiWl7djdeAy3zyUKK1qYkOTaZiJNgO3wR6g71WoDNRAWF8S8psIZhx8fIoecpPGRkjxXBYQtUO6mF6mYMDR+SG+P/18zDqeMbLHPQSkhXpbdFek="

private val NPC_TYPE = NpcType.DESIGNER

class Designer(plugin: SennetMC) : BaseNpc {

    init {
        plugin.commandManager.register(CosmeticsCommand())
    }

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