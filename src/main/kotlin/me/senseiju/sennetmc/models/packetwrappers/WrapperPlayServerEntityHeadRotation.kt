package me.senseiju.sennetmc.models.packetwrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.World
import org.bukkit.entity.Entity

class WrapperPlayServerEntityHeadRotation : AbstractPacket {
    constructor() : super(PacketContainer(TYPE), TYPE) {
        handle.modifier.writeDefaults()
    }

    constructor(packet: PacketContainer?) : super(packet, TYPE) {}
    /**
     * Retrieve Entity ID.
     *
     *
     * Notes: entity's ID
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
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param world - the current world of the entity.
     * @return The spawned entity.
     */
    fun getEntity(world: World?): Entity {
        return handle.getEntityModifier(world!!).read(0)
    }

    /**
     * Retrieve the entity of the painting that will be spawned.
     *
     * @param event - the packet event.
     * @return The spawned entity.
     */
    fun getEntity(event: PacketEvent): Entity {
        return getEntity(event.player.world)
    }
    /**
     * Retrieve Head Yaw.
     *
     *
     * Notes: head yaw in steps of 2p/256
     *
     * @return The current Head Yaw
     */
    /**
     * Set Head Yaw.
     *
     * @param value - new value.
     */
    var headYaw: Byte
        get() = handle.bytes.read(0)
        set(value) {
            handle.bytes.write(0, value)
        }

    companion object {
        val TYPE = PacketType.Play.Server.ENTITY_HEAD_ROTATION
    }
}