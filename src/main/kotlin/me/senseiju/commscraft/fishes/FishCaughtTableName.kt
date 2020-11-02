package me.senseiju.commscraft.fishes

enum class FishCaughtTableName {
    FISH_CAUGHT,
    TOTAL_FISH_CAUGHT;

    override fun toString(): String {
        return super.toString().toLowerCase()
    }
}