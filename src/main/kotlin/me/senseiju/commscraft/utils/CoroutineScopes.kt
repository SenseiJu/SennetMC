package me.senseiju.commscraft.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

val defaultScope = CoroutineScope(Dispatchers.Default)
val mainScope = CoroutineScope(Dispatchers.Unconfined)