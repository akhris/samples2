package persistence.dto

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.UUID

class EntitySampleType(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntitySampleType>(Tables.SampleTypes)

    var name by Tables.SampleTypes.name
    var description by Tables.SampleTypes.description
}

class EntitySample(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EntitySample>(Tables.Samples)

    val sampleID by Tables.Samples.sampleID
    var type by EntitySampleType referencedOn Tables.Samples.type
    var description by Tables.Samples.description
    var comment by Tables.Samples.comment
    var orderID by Tables.Samples.orderID
}
