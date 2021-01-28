package me.senseiju.sennetmc.models.packetwrappers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.World
import org.bukkit.entity.Entity

class WrapperPlayServerEntityLook : AbstractPacket {
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
     * Retrieve the yaw of the current entity.
     *
     * @return The current Yaw
     */
    /**
     * Set the yaw of the current entity.
     *
     * @param value - new yaw.
     */
    var yaw: Float
        get() = handle.bytes.read(0) * 360f / 256.0f
        set(value) {
            handle.bytes.write(0, (value * 256.0f / 360.0f).toInt().toByte())
        }
    /**
     * Retrieve the pitch of the current entity.
     *
     * @return The current pitch
     */
    /**
     * Set the pitch of the current entity.
     *
     * @param value - new pitch.
     */
    var pitch: Float
        get() = handle.bytes.read(1) * 360f / 256.0f
        set(value) {
            handle.bytes.write(1, (value * 256.0f / 360.0f).toInt().toByte())
        }
    /**
     * Retrieve On Ground.
     *
     * @return The current On Ground
     */
    /**
     * Set On Ground.
     *
     * @param value - new value.
     */
    var onGround: Boolean
        get() = handle.booleans.read(0)
        set(value) {
            handle.booleans.write(0, value)
        }

    companion object {
        val TYPE = PacketType.Play.Server.ENTITY_LOOK
    }
}