package me.senseiju.sennetmc.models.packetwrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer

/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http://dmulloy2.net>
 * Copyright (C) Kristian S. Strangeland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


class WrapperPlayServerEntityDestroy : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer?) : super(packet, TYPE) {}

    /**
     * Retrieve Count.
     *
     *
     * Notes: length of following array
     *
     * @return The current Count
     */
    val count: Int
        get() = handle.integerArrays.read(0).size

    /**
     * Retrieve Entity IDs.
     *
     *
     * Notes: the list of entities of destroy
     *
     * @return The current Entity IDs
     */
    val entityIDs: IntArray
        get() = handle.integerArrays.read(0)

    /**
     * Set Entity IDs.
     *
     * @param value - new value.
     */
    fun setEntityIds(value: IntArray?) {
        handle.integerArrays.write(0, value)
    }

    companion object {
        val TYPE = PacketType.Play.Server.ENTITY_DESTROY
    }
}