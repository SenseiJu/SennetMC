package me.senseiju.sennetmc.models

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.*
import javax.sql.rowset.CachedRowSet
import kotlin.collections.ArrayList

enum class ModelType(val material: Material) {
    HAT(Material.BEDROCK),
    FISHING_ROD(Material.FISHING_ROD),
    BACKPACK(Material.OBSIDIAN),
    SLEEVE(Material.STONE);

    companion object {
        fun modelsFromSet(set: CachedRowSet) : EnumMap<ModelType, ArrayList<Int>> {
            val models = EnumMap<ModelType, ArrayList<Int>>(ModelType::class.java)

            while (set.next()) {
                val modelType = valueOf(set.getString("model_type"))
                val modelData = set.getInt("model_data")

                models.computeIfAbsent(modelType) { ArrayList() }.add(modelData)
            }

            return models
        }

        fun activeModelsFromSet(set: CachedRowSet) : EnumMap<ModelType, Int> {
            val models = EnumMap<ModelType, Int>(ModelType::class.java)

            while (set.next()) {
                val modelType = valueOf(set.getString("model_type"))
                val modelData = set.getInt("model_data")

                models[modelType] = modelData
            }

            return models
        }

        fun isItemModel(itemStack: ItemStack) : Boolean {
            if (!values().map { it.material }.contains(itemStack.type)) return false

            return itemStack.itemMeta.hasCustomModelData()
        }
    }
}