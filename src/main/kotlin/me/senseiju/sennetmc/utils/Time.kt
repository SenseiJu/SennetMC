package me.senseiju.sennetmc.utils

import java.util.concurrent.TimeUnit

fun secondsToTimeFormat(seconds: Long) : String {
    val hours = TimeUnit.SECONDS.toHours(seconds)
    val minute = TimeUnit.SECONDS.toMinutes(seconds) - hours * 60
    val second = TimeUnit.SECONDS.toSeconds(seconds) - hours * 3600 - minute * 60

    return "%02d:%02d:%02d".format(hours, minute, second)
}