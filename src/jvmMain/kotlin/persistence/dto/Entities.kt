package persistence.dto

import org.jetbrains.exposed.dao.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
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

class EntityParameter(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntityParameter>(Tables.Parameters)

    val name by Tables.Parameters.name
    val sampleType by EntitySampleType referencedOn Tables.Parameters.sampleType
    val description by Tables.Parameters.description
    val position by Tables.Parameters.position
}