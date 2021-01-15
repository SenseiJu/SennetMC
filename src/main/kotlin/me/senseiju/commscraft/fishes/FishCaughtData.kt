package me.senseiju.commscraft.fishes

class FishCaughtData(var current: Int = 0, var total: Int = 0) {
    operator fun plus(increment: Int) {
        current += increment
        total += increment
    }
}
