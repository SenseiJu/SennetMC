package me.senseiju.sennetmc.utils.extensions

import me.senseiju.sentils.extensions.primitives.color

val Boolean.int
    get() = if (this) 1 else 0

val Boolean.string
    get() = if (this) "&a&lTrue".color() else "&c&lFalse".color()