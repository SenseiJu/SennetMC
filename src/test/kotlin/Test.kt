import me.senseiju.sennetmc.equipment.Equipment
import me.senseiju.sennetmc.npcs.types.scrapper.CraftableEquipment
import org.junit.jupiter.api.Test
import java.util.*

class Test {

    @Test
    fun doIt() {
        val craftableEquipment = CraftableEquipment(
            Equipment.FISHING_NET,
            200,
            500,
            UUID.randomUUID(),
            10000,
            false
            )

        val new = CraftableEquipment.fromJson(craftableEquipment.toJson())

        println(new.toJson())
    }

}