package me.senseiju.sennetmc.npcs.types.scrapper

import me.senseiju.sennetmc.equipment.Equipment

abstract class CraftableEquipment : Craftable() {
    abstract val equipment: Equipment
}