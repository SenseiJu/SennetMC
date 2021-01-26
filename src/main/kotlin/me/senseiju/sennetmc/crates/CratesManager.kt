package me.senseiju.sennetmc.crates

import de.tr7zw.changeme.nbtapi.NBTItem
import me.mattstudios.mf.base.CommandManager
import me.mattstudios.mf.base.CompletionHandler
import me.senseiju.sennetmc.BaseManager
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.crates.commands.CombineCratesCommand
import me.senseiju.sennetmc.crates.commands.CratesCommand
import me.senseiju.sennetmc.crates.listeners.CrateOpenListener
import me.senseiju.sennetmc.crates.listeners.PlayerCaughtFishListener
import me.senseiju.sennetmc.datastorage.DataFile
import me.senseiju.sennetmc.fishes.events.PlayerCaughtFishEvent
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.User
import me.senseiju.sennetmc.utils.percentChance
import me.senseiju.sennetmc.utils.probabilityChance
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CratesManager(private val plugin: SennetMC) : BaseManager {
    private val cratesFile = DataFile(plugin, "crates.yml", true)
    var cratesMap = HashMap<String, Crate>()
        private set

    private val users = plugin.userManager.userMap
    private val upgradesFile = plugin.upgradesManager.upgradesFile

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
        PlayerCaughtFishListener(plugin, this)
    }

    override fun reload() {
        cratesFile.reload()

        loadCrates()
    }

    fun isItemCrate(itemStack: ItemStack): Boolean {
        return itemStack.type == Material.CHEST && NBTItem(itemStack).hasKey("crate-id")
    }

    fun getCrateFromItem(itemStack: ItemStack): Crate? = cratesMap[NBTItem(itemStack).getString("crate-id")]

    fun selectRandomCrate(increasedProbability: Double = 0.0): Crate =
            probabilityChance(cratesMap.values.map { it to (it.probabilityPerCast + increasedProbability) }.toMap())

    fun combineCrates(player: Player) {
        val currentCrates = HashMap<String, Int>()

        @Suppress("UselessCallOnCollection")
        player.inventory.contents.filterNotNull().forEach {
            if (it.type != Material.CHEST) return@forEach

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
            val probabilityPerCast = section.getDouble("probability-per-cast", 0.0)
            val maxCratesPerCast = section.getInt("max-crates-per-cast", 0)

            newCratesMap[it] = Crate(it, name, description, upgradeId, rewards, probabilityPerCast, maxCratesPerCast)
        }

        cratesMap = newCratesMap
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadRewards(rewardsMapList: List<Map<*, *>>): List<Reward> {
        val newRewardsList = ArrayList<Reward>()

        rewardsMapList.forEach {
            val probability = if (it["probability"] is Int) it["probability"] as Int else it["probability"] as Double
            newRewardsList.add(Reward(it["name"] as String, probability.toDouble(), it["commands"] as List<String>))
        }

        return newRewardsList
    }

    fun handleCratesOnFish(player: Player) {
        val user = users[player.uniqueId] ?: return

        if (!shouldPlayerReceiveCrates(user)) {
            return
        }

        val crate = selectRandomCrate(getPlayerCrateMasterProbabilityIncrease(user))
        val amount = crate.generateRandomNumberOfCrates()

        if (shouldPlayerReceiveDoubleCrates(user)) {
            crate.giveCrate(player, amount * 2)
        } else {
            crate.giveCrate(player, amount)
        }
    }

    private fun getPlayerCrateMasterProbabilityIncrease(user: User) : Double {
        return user.getUpgrade(Upgrade.CRATE_MASTER) * upgradesFile.config.getDouble("crate-master-upgrade-increment", 0.4)
    }

    private fun shouldPlayerReceiveCrates(user: User) : Boolean {
        val baseChance = cratesFile.config.getDouble("chance-to-receive-crates", 0.1)
        val discoveryChance = user.getUpgrade(Upgrade.DISCOVERY)
                .times(upgradesFile.config.getDouble("discovery-upgrade-increment", 0.01))

        return percentChance(baseChance + discoveryChance)
    }

    private fun shouldPlayerReceiveDoubleCrates(user: User) : Boolean {
        val treasureFinderChance = user.getUpgrade(Upgrade.TREASURE_FINDER)
                .times(upgradesFile.config.getDouble("treasure-finder-upgrade-increment", 0.01))

        return percentChance(treasureFinderChance)
    }
}