package me.senseiju.sennetmc.users

import me.senseiju.sennetmc.fishes.FishCaughtData
import me.senseiju.sennetmc.fishes.FishType
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.utils.extensions.round
import java.time.Instant
import java.util.*

class User(
    val uuid: UUID,
    val collectables: ArrayList<String> = ArrayList(),
    val fishCaught: EnumMap<FishType, FishCaughtData> = EnumMap(FishType::class.java),
    val upgrades: EnumMap<Upgrade, Int> = EnumMap(Upgrade::class.java),
    val settings: EnumMap<Setting, Boolean> = EnumMap(Setting::class.java),
    var dailyRewardLastClaimed: Instant = Instant.ofEpochMilli(0)
) {

    val currentFishCaughtCapacity
        get() = fishCaught.entries.sumOf { it.key.capacity() * it.value.current }

    val totalFishCaught
        get() = fishCaught.entries.sumOf { it.value.total }

    fun addToCurrentFish(fishType: FishType, amount: Int = 1) {
        fishCaught.computeIfAbsent(fishType) { FishCaughtData() }.plus(amount)
    }

    fun toggleSetting(setting: Setting) {
        settings[setting] = !getSetting(setting)
    }

    fun getSetting(setting: Setting): Boolean = settings.computeIfAbsent(setting) { setting.default }

    fun incrementUpgrade(upgrade: Upgrade, amount: Int = 1) {
        upgrades.merge(upgrade, amount, Int::plus)
    }

    fun getUpgrade(upgrade: Upgrade): Int = upgrades.computeIfAbsent(upgrade) { 0 }

    fun calculateSellPrice(multiplier: Double = 1.0): Double {
        return fishCaught.map { (type, data) -> type.selectRandomSellPrice() * data.current }
            .sum()
            .times(multiplier)
            .round()
    }

    fun clearCurrentFishCaught() {
        fishCaught.forEach { (_, data) ->
            data.current = 0
        }
    }
}