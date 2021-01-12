package me.senseiju.commscraft.crates

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.CompletionHandler
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.commands.CombineCratesCommand
import me.senseiju.commscraft.crates.commands.CratesCommand
import me.senseiju.commscraft.crates.listeners.CrateOpenListener
import me.senseiju.commscraft.crates.listeners.PlayerFishListener
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.utils.probabilityChance
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.random.Random

class CratesManager(private val plugin: CommsCraft) : BaseManager {

    val cratesFile = DataFile(plugin, "crates.yml", true)

    var cratesMap = HashMap<String, Crate>()
        private set

    init {
        loadCrates()

        registerCommands(plugin.commandManager)
        registerEvents()
    }
    
    override fun registerCommands(cm: CommandManager) {
        registerCommandCompletions(cm.completionHandler)

        cm.register(CratesCommand(plugin, this))
        cm.register(CombineCratesCommand(this))
    }

    private fun registerCommandCompletions(ch: CompletionHandler) {
        ch.register("#crateId") { cratesMap.keys.toList() }
    }

    override fun registerEvents() {
        CrateOpenListener(plugin, this)
        PlayerFishListener(plugin, this)
    }

    override fun reload() {
        cratesFile.reload()

        loadCrates()
    }

    fun selectRandomCrate() : Crate = probabilityChance(cratesMap.values.map { it to it.probabilityPerCast }.toMap())

    fun combineCrates(player: Player) {
        val currentCrates = HashMap<String, Int>()
        player.inventory.contents.forEach {
            @Suppress("SENSELESS_COMPARISON")
            if (it == null || it.type != Material.CHEST) return@forEach

            val nbtItem = NBTItem(it)
            if (!nbtItem.hasKey("crate-id")) return@forEach

            val crateId = nbtItem.getString("crate-id")
            if (!cratesMap.containsKey(crateId)
                    || cratesMap[crateId]?.upgradeId.equals("null", true)) return@forEach

            if (!currentCrates.containsKey(crateId)) currentCrates[crateId] = it.amount
            else currentCrates[crateId] = currentCrates[crateId]!!.plus(it.amount)

            it.amount = 0
        }

        currentCrates.forEach {
            val crate = cratesMap[it.key]
            val upgradedCrate = cratesMap[crate?.upgradeId]

            crate?.giveCrate(player, it.value % cratesFile.config.getInt("crates-required-before-upgrade", 4))
            upgradedCrate?.giveCrate(player, it.value / cratesFile.config.getInt("crates-required-before-upgrade", 4))
        }
    }

    private fun calculateCrateProbabilityRange() : Int {
        var range = 1
        cratesMap.forEach { range += it.value.probabilityPerCast }
        return range
    }
    
    private fun loadCrates() {
        val newCratesMap = HashMap<String, Crate>()

        val description = cratesFile.config.getStringList("description")
        val cratesSection = cratesFile.config.getConfigurationSection("crates")
        cratesSection?.getKeys(false)?.forEach {
            val section = cratesSection.getConfigurationSection(it)
            if (section == null) {
                println("ERROR: Failed to parse a crate with id: $it")
                return@forEach
            }

            val name = section.getString("name", "NO-NAME-SET")!!
            val upgradeId = section.getString("upgraded-crate-id", "NULL")!!
            val rewards = loadRewards(section.getMapList("rewards"))
            val probabilityPerCast = section.getInt("probability-per-cast", 0)
            val maxCratesPerCast = section.getInt("max-crates-per-cast", 0)

            newCratesMap[it] = Crate(it, name, description, upgradeId, rewards,probabilityPerCast, maxCratesPerCast)
        }

        cratesMap = newCratesMap
    }

    private fun loadRewards(rewardsMapList: List<Map<*, *>>) : List<Reward> {
        val newRewardsList = ArrayList<Reward>()

        rewardsMapList.forEach {
            @Suppress("UNCHECKED_CAST")
            newRewardsList.add(Reward(it["name"] as String, it["probability"] as Int, it["commands"] as List<String>))
        }

        return newRewardsList
    }
}