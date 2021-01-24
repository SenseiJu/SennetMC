package me.senseiju.sennetmc.extensions

val Boolean.int
    get() = if (this) 1 else 0

val Boolean.string
    get() = if (this) "&a&lTrue".color() else "&c&lFalse".color()