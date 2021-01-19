package me.senseiju.commscraft.models

import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.models.commands.ModelCommand
import me.senseiju.commscraft.npcs.types.NpcType
import org.bukkit.inventory.ItemStack
import java.util.*

class ModelsManager(private val plugin: CommsCraft) : BaseManager {

    private val modelsFile = DataFile(plugin, "models.yml", true)

    var models = EnumMap<ModelType, Map<Int, ItemStack>>(ModelType::class.java)
        private set

    init {
        registerCommands(plugin.commandManager)
        registerEvents()

        loadModels()
    }

    override fun registerCommands(cm: CommandManager) {
        registerCommandParameters(cm.parameterHandler)

        cm.register(ModelCommand(plugin, this))
    }

    override fun registerEvents() {
    }

    private fun registerCommandParameters(ph: ParameterHandler) {
        ph.register(ModelType::class.java, ParameterResolver { argument ->
            try {
                return@ParameterResolver TypeResult(ModelType.valueOf(argument.toString().toUpperCase()), argument)
            } catch (ex: IllegalArgumentException) {
                return@ParameterResolver TypeResult(argument)
            }
        })
    }

    override fun reload() {
        modelsFile.reload()

        loadModels()
    }

    suspend fun fetchModels(uuid: UUID) : EnumMap<ModelType, ArrayList<Int>> {
        val set = plugin.database.asyncQuery("SELECT * FROM `models` WHERE `uuid`=?;", uuid.toString())

        return ModelType.modelsFromSet(set)
    }

    fun updateModels(uuid: UUID, models: EnumMap<ModelType, ArrayList<Int>>) {
        val deleteQuery = "DELETE FROM `models` WHERE `uuid`=?;"
        plugin.database.updateQuery(deleteQuery, uuid.toString())

        val insertQuery = "INSERT INTO `models`(`uuid`, `model_type`, `model_data`) VALUES(?,?,?);"
        models.forEach { (modelType, modelDataList) ->
            modelDataList.forEach { modelData ->
                plugin.database.updateQuery(insertQuery, uuid.toString(), modelType.toString(), modelData)
            }
        }
    }

    private fun createModelItem(modelType: ModelType, modelData: Int, modelName: String) : ItemStack {
        val item = ItemBuilder.from(modelType.material)
                .setName(modelName.color())
                .build()

        val meta = item.itemMeta
        meta.setCustomModelData(modelData)
        item.itemMeta = meta

        return item
    }

    private fun loadModels() {
        val newModels = EnumMap<ModelType, Map<Int, ItemStack>>(ModelType::class.java)

        modelsFile.config.getKeys(false).forEach { modelTypeKey ->
            val section = modelsFile.config.getConfigurationSection(modelTypeKey) ?: return@forEach
            val modelType = ModelType.valueOf(modelTypeKey)
            val modelDataItems = HashMap<Int, ItemStack>()

            section.getKeys(false).forEach { modelDataKey ->
                val modelData = modelDataKey.toInt()
                val item = createModelItem(modelType, modelData, section.getString(modelDataKey, "NO NAME SET")!!)
                modelDataItems[modelData] = item
            }

            newModels[modelType] = modelDataItems
        }

        models = newModels
    }
}