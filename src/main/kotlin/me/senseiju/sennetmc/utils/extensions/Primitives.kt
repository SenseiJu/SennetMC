package me.senseiju.sennetmc.utils.extensions

import java.text.DecimalFormat

private val decimalFormat = run {
    with (DecimalFormat("###,###.##")) {
        groupingSize = 3
        isGroupingUsed = true

        this
    }
}

/**
 * Rounds the double to some number of decimal places
 *
 * @param dp The number of decimal places to round to
 *
 * @return the rounded number
 */
fun Double.round(dp: Int = 2): Double {
    return "%.${dp}f".format(this).toDouble()
}

fun Long.decimalFormat(): String {
    return decimalFormat.format(this)
}

fun Double.decimalFormat(): String {
    return decimalFormat.format(this)
}