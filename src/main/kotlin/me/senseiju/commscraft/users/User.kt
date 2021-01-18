package me.senseiju.commscraft.users

import me.senseiju.commscraft.fishes.FishCaughtData
import me.senseiju.commscraft.fishes.FishType
import me.senseiju.commscraft.settings.Setting
import me.senseiju.commscraft.upgrades.Upgrade
import java.util.*
import kotlin.collections.ArrayList

class User(val uuid: UUID,
           val collectables: ArrayList<String> = ArrayList(),
           val fishCaught: EnumMap<FishType, FishCaughtData> = EnumMap(FishType::class.java),
           val upgrades: EnumMap<Upgrade, Int> = EnumMap(Upgrade::class.java),
           val settings: EnumMap<Setting, Boolean> = EnumMap(Setting::class.java) ) {

    val currentFishCaughtCapacity get() = fishCaught.entries.sumBy { it.key.capacity() * it.value.current }

    fun incrementUpgrade(upgrade: Upgrade, amount: Int = 1) { upgrades.merge(upgrade, amount, Int::plus) }

    fun addToCurrentFish(fishType: FishType, amount: Int) { fishCaught.computeIfAbsent(fishType) { FishCaughtData() }.plus(amount) }

    fun toggleSetting(setting: Setting) { settings[setting] = !settings.getOrDefault(setting, setting.default) }

}