package me.senseiju.commscraft.models

import org.bukkit.Material
import java.util.*
import javax.sql.rowset.CachedRowSet
import kotlin.collections.ArrayList

enum class ModelType(val material: Material) {
    HELMET(Material.BEDROCK),
    FISHING_ROD(Material.FISHING_ROD);

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
    }
}