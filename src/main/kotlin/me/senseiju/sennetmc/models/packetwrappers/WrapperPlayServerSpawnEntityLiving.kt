/**
 * PacketWrapper - ProtocolLib wrappers for Minecraft packets
 * Copyright (C) dmulloy2 <http:></http:>//dmulloy2.net>
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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package me.senseiju.sennetmc.models.packetwrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.injector.PacketConstructor
import com.comphenix.protocol.wrappers.WrappedDataWatcher
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import java.util.*

class WrapperPlayServerSpawnEntityLiving : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer?) : super(packet, TYPE) {}
    constructor(entity: Entity) : super(fromEntity(entity), TYPE) {}
    /**
     * Retrieve entity ID.
     *
     * @return The current EID
     */
    /**
     * Set entity ID.
     *
     * @param value - new value.
     */
    var entityID: Int
        get() = handle.integers.read(0)
        set(value) {
            handle.integers.write(0, value)
        }

    /**
     * Retrieve the entity that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    fun getEntity(world: World?): Entity {
        return handle.getEntityModifier(world!!).read(0)
    }

    /**
     * Retrieve the entity that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    fun getEntity(event: PacketEvent): Entity {
        return getEntity(event.player.world)
    }

    var uniqueId: UUID?
        get() = handle.uuiDs.read(0)
        set(value) {
            handle.uuiDs.write(0, value)
        }

    /**
     * Retrieve the type of mob.
     *
     * @return The current Type
     */
    val type: EntityType?
        get() = EntityType.fromId(handle.integers.read(1))

    /**
     * Set the type of mob.
     *
     * @param value - new value.
     */
    fun setType(value: EntityType) {
        handle.integers.write(1, value.typeId.toInt())
    }
    /**
     * Retrieve the x position of the object.
     *
     *
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     *
     * @return The current X
     */
    /**
     * Set the x position of the object.
     *
     * @param value - new value.
     */
    var x: Double
        get() = handle.doubles.read(0)
        set(value) {
            handle.doubles.write(0, value)
        }
    /**
     * Retrieve the y position of the object.
     *
     *
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     *
     * @return The current y
     */
    /**
     * Set the y position of the object.
     *
     * @param value - new value.
     */
    var y: Double
        get() = handle.doubles.read(1)
        set(value) {
            handle.doubles.write(1, value)
        }
    /**
     * Retrieve the z position of the object.
     *
     *
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     *
     * @return The current z
     */
    /**
     * Set the z position of the object.
     *
     * @param value - new value.
     */
    var z: Double
        get() = handle.doubles.read(2)
        set(value) {
            handle.doubles.write(2, value)
        }
    /**
     * Retrieve the yaw.
     *
     * @return The current Yaw
     */
    /**
     * Set the yaw of the spawned mob.
     *
     * @param value - new yaw.
     */
    var yaw: Float
        get() = handle.bytes.read(0) * 360f / 256.0f
        set(value) {
            handle.bytes.write(0, (value * 256.0f / 360.0f).toByte())
        }
    /**
     * Retrieve the pitch.
     *
     * @return The current pitch
     */
    /**
     * Set the pitch of the spawned mob.
     *
     * @param value - new pitch.
     */
    var pitch: Float
        get() = handle.bytes.read(1) * 360f / 256.0f
        set(value) {
            handle.bytes.write(1, (value * 256.0f / 360.0f).toByte())
        }
    /**
     * Retrieve the yaw of the mob's head.
     *
     * @return The current yaw.
     */
    /**
     * Set the yaw of the mob's head.
     *
     * @param value - new yaw.
     */
    var headPitch: Float
        get() = handle.bytes.read(2) * 360f / 256.0f
        set(value) {
            handle.bytes.write(2, (value * 256.0f / 360.0f).toByte())
        }
    /**
     * Retrieve the velocity in the x axis.
     *
     * @return The current velocity X
     */
    /**
     * Set the velocity in the x axis.
     *
     * @param value - new value.
     */
    var velocityX: Double
        get() = handle.integers.read(2) / 8000.0
        set(value) {
            handle.integers.write(2, (value * 8000.0).toInt())
        }
    /**
     * Retrieve the velocity in the y axis.
     *
     * @return The current velocity y
     */
    /**
     * Set the velocity in the y axis.
     *
     * @param value - new value.
     */
    var velocityY: Double
        get() = handle.integers.read(3) / 8000.0
        set(value) {
            handle.integers.write(3, (value * 8000.0).toInt())
        }
    /**
     * Retrieve the velocity in the z axis.
     *
     * @return The current velocity z
     */
    /**
     * Set the velocity in the z axis.
     *
     * @param value - new value.
     */
    var velocityZ: Double
        get() = handle.integers.read(4) / 8000.0
        set(value) {
            handle.integers.write(4, (value * 8000.0).toInt())
        }
    /**
     * Retrieve the data watcher. This was removed in 1.15
     *
     *
     * Content varies by mob, see Entities.
     *
     * @return The current Metadata
     */
    /**
     * Set the data watcher. This was removed in 1.15.
     *
     * @param value - new value.
     */
    var metadata: WrappedDataWatcher?
        get() = handle.dataWatcherModifier.read(0)
        set(value) {
            handle.dataWatcherModifier.write(0, value)
        }

    companion object {
        val TYPE = PacketType.Play.Server.SPAWN_ENTITY_LIVING
        private var entityConstructor: PacketConstructor? = null

        // Useful constructor
        private fun fromEntity(entity: Entity): PacketContainer {
            if (entityConstructor == null) entityConstructor = ProtocolLibrary.getProtocolManager()
                    .createPacketConstructor(TYPE, entity)
            return entityConstructor!!.createPacket(entity)
        }
    }
}