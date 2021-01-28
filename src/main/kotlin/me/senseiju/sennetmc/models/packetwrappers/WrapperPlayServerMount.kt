package me.senseiju.sennetmc.models.packetwrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.World
import org.bukkit.entity.Entity
import java.util.*

class WrapperPlayServerMount : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer?) : super(packet, TYPE) {}
    /**
     * Retrieve Entity ID.
     *
     *
     * Notes: vehicle's EID
     *
     * @return The current Entity ID
     */
    /**
     * Set Entity ID.
     *
     * @param value - new value.
     */
    var entityID: Int
        get() = handle.integers.read(0)
        set(value) {
            handle.integers.write(0, value)
        }

    /**
     * Retrieve the entity involved in this event.
     *
     * @param world - the current world of the entity.
     * @return The involved entity.
     */
    fun getEntity(world: World?): Entity {
        return handle.getEntityModifier(world!!).read(0)
    }

    /**
     * Retrieve the entity involved in this event.
     *
     * @param event - the packet event.
     * @return The involved entity.
     */
    fun getEntity(event: PacketEvent): Entity {
        return getEntity(event.player.world)
    }

    var passengerIds: IntArray?
        get() = handle.integerArrays.read(0)
        set(value) {
            handle.integerArrays.write(0, value)
        }

    fun getPassengers(event: PacketEvent): List<Entity> {
        return getPassengers(event.player.world)
    }

    fun getPassengers(world: World?): List<Entity> {
        val ids = passengerIds!!
        val passengers: MutableList<Entity> = ArrayList()
        val manager = ProtocolLibrary.getProtocolManager()
        for (id in ids) {
            val entity = manager.getEntityFromID(world, id)
            if (entity != null) {
                passengers.add(entity)
            }
        }
        return passengers
    }

    fun setPassengers(value: List<Entity>) {
        val array = IntArray(value.size)
        for (i in value.indices) {
            array[i] = value[i].entityId
        }
        passengerIds = array
    }

    companion object {
        val TYPE = PacketType.Play.Server.MOUNT
    }
}