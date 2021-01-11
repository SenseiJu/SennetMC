package me.senseiju.commscraft.crates

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.commands.CombineCratesCommand
import me.senseiju.commscraft.crates.commands.CratesGiveCommand
import me.senseiju.commscraft.crates.listeners.CrateOpenListener
import me.senseiju.commscraft.crates.listeners.PlayerFishListener
import me.senseiju.commscraft.datastorage.DataFile
import me.senseiju.commscraft.extensions.addItemOrDropNaturally
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.random.Random

class CratesManager(private val plugin: CommsCraft) : BaseManager {

    var cratesFile = DataFile(plugin, "crates.yml", true)
    
    var cratesMap = HashMap<String, Crate>()

    init {
        loadCrates()

        registerCommands(plugin.commandManager)
        registerEvents()
    }
    
    override fun registerCommands(cm: CommandManager) {
        cm.register(CratesGiveCommand(plugin, this))
        cm.register(CombineCratesCommand(plugin, this))
    }

    override fun registerEvents() {
        CrateOpenListener(plugin, this)
        PlayerFishListener(plugin, this)
    }

    override fun reload() {
        cratesFile.reload()

        loadCrates()
    }

    fun selectRandomCrate() : Crate {
        val random = Random.nextInt(1, calculateCrateProbabilityRange())
        var index = 0
        val cratesList = cratesMap.values.toList()
        var probabilityCount = cratesList[index].probabilityPerCast
        while (true) {
            if (random <= probabilityCount) {
                return cratesList[index]
            }
            probabilityCount += cratesList[++index].probabilityPerCast
        }
    }

    fun combineCrates(player: Player) {
        val currentCrates = HashMap<String, Int>()
        player.inventory.contents.forEach loop@ {
            if (it == null || it.type != Material.CHEST) return@loop

            val nbtItem = NBTItem(it)
            if (!nbtItem.hasKey("crate-id")) return@loop

            val crateId = nbtItem.getString("crate-id")
            if (!cratesMap.containsKey(crateId)
                || cratesMap[crateId]?.upgradeId.equals("null", true)) return@loop

            if (currentCrates.containsKey(crateId)) {
                currentCrates[crateId]?.plus(it.amount)
            } else {
                currentCrates[crateId] = it.amount
            }

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

        val cratesSection = cratesFile.config.getConfigurationSection("crates")
        cratesSection?.getKeys(false)?.forEach loop@ {
            val section = cratesSection.getConfigurationSection(it)
            if (section == null) {
                println("ERROR: Failed to parse a crate")
                return@loop
            }
            val crate = Crate(
                it,
                section.getString("name", "NO-NAME-SET")!!,
                cratesFile.config.getStringList("description"),
                section.getString("upgraded-crate-id", "NULL")!!,
                section.getInt("probability-per-cast", 0),
                section.getInt("max-crates-per-cast", 0)
            )

            loadRewards(crate, section.getMapList("rewards"))

            newCratesMap[it] = crate
        }

        cratesMap = newCratesMap
    }

    private fun loadRewards(crate: Crate, rewardsMapList: List<Map<*, *>>) {
        val newRewardsList = ArrayList<Reward>()

        rewardsMapList.forEach {
            newRewardsList.add(Reward(it["name"] as String, it["probability"] as Int, it["commands"] as List<String>))
        }

        crate.rewards = newRewardsList
    }
}