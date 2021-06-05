package me.senseiju.sennetmc.utils

class CooldownManager<K>(private val cooldown: Long) {
    private val cooldowns = hashMapOf<K, Long>()

    fun start(key: K) {
        cooldowns[key] = System.currentTimeMillis()
    }

    fun isOnCooldown(key: K): Boolean {
        if (!cooldowns.containsKey(key)) {
            return false
        }

        return System.currentTimeMillis() - cooldowns[key]!! <= cooldown
    }

    fun timeRemaining(key: K): Long {
       return (cooldown - (System.currentTimeMillis() - cooldowns.getOrDefault(key, 0))) / 1000
    }
}