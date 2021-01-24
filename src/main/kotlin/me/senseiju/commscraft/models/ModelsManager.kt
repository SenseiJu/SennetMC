package me.senseiju.commscraft.models

import kotlinx.coroutines.launch
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.ParameterHandler
import me.mattstudios.mf.base.components.ParameterResolver
import me.mattstudios.mf.base.components.TypeResult
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.extensions.color
import me.senseiju.commscraft.extensions.sendConfigMessage
import me.senseiju.commscraft.models.commands.HatCommand
import me.senseiju.commscraft.models.commands.ModelCommand
import me.senseiju.commscraft.models.listeners.BackpackListener
import me.senseiju.commscraft.models.listeners.HelmetListener
import me.senseiju.commscraft.models.listeners.SleeveListener
import me.senseiju.commscraft.utils.ObjectSet
import me.senseiju.commscraft.utils.defaultScope
import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DUPLICATES")
class ModelsManager(private val plugin: CommsCraft) : BaseManager {

    private val modelsFile = DataFile(plugin, "models.yml", true)

    var models = EnumMap<ModelType, Map<Int, Model>>(ModelType::class.java)
        private set

    init {
        loadModels()

        registerCommands(plugin.commandManager)
        registerEvents()
    }

    override fun registerCommands(cm: CommandManager) {
        registerCommandParameters(cm.parameterHandler)

        cm.register(ModelCommand(plugin, this))
        cm.register(HatCommand(plugin))
    }

    override fun registerEvents() {
        HelmetListener(plugin, this)
        SleeveListener(plugin, this)
        BackpackListener(plugin)
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

    fun addModel(targetUUID: UUID, modelType: ModelType?, modelData: Int?, sender: CommandSender?) {
        defaultScope.launch {
            if (modelType == null) {
                sender?.sendConfigMessage("MODELS-INVALID-MODEL-TYPE")
                return@launch
            }

            val models = models.computeIfAbsent(modelType) { HashMap() }
            if (!models.containsKey(modelData)) {
                sender?.sendConfigMessage("MODELS-CANNOT-FIND-MODEL")
                return@launch
            }

            val user = plugin.userManager.userMap[targetUUID] ?: return@launch
            val userModels = user.models.computeIfAbsent(modelType) { ArrayList() }
            if (userModels.contains(modelData)) {
                sender?.sendConfigMessage("MODELS-TARGET-HAS-MODEL")
                return@launch
            }

            user.models[modelType]?.add(modelData!!)

            sender?.sendConfigMessage("MODELS-MODEL-SET")

            val targetPlayer = plugin.server.getPlayer(user.uuid) ?: return@launch
            targetPlayer.sendConfigMessage("MODELS-TARGET-MODEL-SET",
                    ObjectSet("{modelName}", models[modelData]!!.modelName.color()))
        }
    }

    fun removeModel(targetUUID: UUID, modelType: ModelType?, modelData: Int?, sender: CommandSender?) {
        defaultScope.launch {
            if (modelType == null) {
                sender?.sendConfigMessage("MODELS-INVALID-MODEL-TYPE")
                return@launch
            }

            val models = models.computeIfAbsent(modelType) { HashMap() }
            if (!models.containsKey(modelData)) {
                sender?.sendConfigMessage("MODELS-CANNOT-FIND-MODEL")
                return@launch
            }

            val user = plugin.userManager.userMap[targetUUID] ?: return@launch
            val userModels = user.models.computeIfAbsent(modelType) { ArrayList() }
            if (!userModels.contains(modelData)) {
                sender?.sendConfigMessage("MODELS-TARGET-DOES-NOT-HAVE-MODEL")
                return@launch
            }

            user.models[modelType]?.remove(modelData!!)

            sender?.sendConfigMessage("MODELS-MODEL-REMOVED")
        }
    }

    suspend fun fetchActiveModels(uuid: UUID) : EnumMap<ModelType, Int> {
        val set = plugin.database.asyncQuery("SELECT * FROM `active_models` WHERE `uuid`=?;", uuid.toString())

        return ModelType.activeModelsFromSet(set)
    }

    suspend fun fetchModels(uuid: UUID) : EnumMap<ModelType, ArrayList<Int>> {
        val set = plugin.database.asyncQuery("SELECT * FROM `models` WHERE `uuid`=?;", uuid.toString())

        return ModelType.modelsFromSet(set)
    }

    fun updateActiveModels(uuid: UUID, models: EnumMap<ModelType, Int>) {
        val q = "INSERT INTO `active_models`(`uuid`, `model_type`, `model_data`) VALUES(?,?,?)" +
                "ON DUPLICATE KEY UPDATE `model_data`=?;"
        models.forEach { (modelType, modelData) ->
            plugin.database.updateQuery(q, uuid.toString(), modelType.toString(), modelData, modelData)
        }
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
        val newModels = EnumMap<ModelType, Map<Int, Model>>(ModelType::class.java)

        modelsFile.config.getKeys(false).forEach { modelTypeKey ->
            val section = modelsFile.config.getConfigurationSection(modelTypeKey) ?: return@forEach
            val modelType = ModelType.valueOf(modelTypeKey)
            val modelDataItems = HashMap<Int, Model>()

            section.getKeys(false).forEach { modelDataKey ->
                val modelData = modelDataKey.toInt()
                val modelName = section.getString(modelDataKey, "NO NAME SET")!!
                val item = createModelItem(modelType, modelData, modelName)

                modelDataItems[modelData] = Model(modelType, modelData, modelName, item)
            }

            newModels[modelType] = modelDataItems
        }

        models = newModels
    }
}