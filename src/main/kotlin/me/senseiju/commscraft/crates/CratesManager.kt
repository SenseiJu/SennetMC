package me.senseiju.commscraft.crates

import me.mattstudios.mf.base.CommandManager
import me.senseiju.commscraft.BaseManager
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.crates.commands.CratesGiveCommand
import me.senseiju.commscraft.crates.listeners.CrateOpenListener
import me.senseiju.commscraft.datastorage.DataFile

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
    }

    override fun registerEvents() {
        CrateOpenListener(plugin, this)
    }

    override fun reload() {
        cratesFile.reload()

        loadCrates()
    }
    
    private fun loadCrates() {
        val newCratesMap = HashMap<String, Crate>()

        cratesFile.config.getConfigurationSection("crates")?.getKeys(false)?.forEach loop@ {
            val section = cratesFile.config.getConfigurationSection(it)
            if (section == null) {
                println("ERROR: Failed to parse a crate")
                return@loop
            }
            val crate = Crate(it,
                section.getString("name", "NO-NAME-SET")!!,
                cratesFile.config.getStringList("description"),
                section.getInt("crates-required-before-upgrade", 4),
                section.getString("upgraded-crate-id"))

            loadRewards(crate)

            newCratesMap[it] = crate
        }

        cratesMap = newCratesMap
    }

    private fun loadRewards(crate: Crate) {
        val newRewardsList = ArrayList<Reward>()

        val section = cratesFile.config.getConfigurationSection(crate.id)
        if (section == null) {
            println("ERROR: Failed to parse a crates rewards")
            return
        }
        val rewardsMapList = section.getMapList("rewards")
        rewardsMapList.forEach {
            newRewardsList.add(Reward(it["name"] as String, it["probability"] as Int, it["commands"] as List<String>))
        }

        crate.rewards = newRewardsList
    }
}