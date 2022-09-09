package persistence.dto

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class EntitySampleType(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntitySampleType>(Tables.SampleTypes)

    var name by Tables.SampleTypes.name
    var description by Tables.SampleTypes.description
}

class EntitySample(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntitySample>(Tables.Samples)

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

class EntityPlace(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntityPlace>(Tables.Places)

    val name by Tables.Places.name
    val roomNumber by Tables.Places.roomNumber
    val description by Tables.Places.description
}

class EntityWorker(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntityWorker>(Tables.Workers)

    val name by Tables.Workers.name
    val middleName by Tables.Workers.middleName
    val surname by Tables.Workers.surname
    val room by EntityPlace optionalReferencedOn Tables.Workers.room
    val phoneNumber by Tables.Workers.phoneNumber
    val email by Tables.Workers.email
}

class EntityOperationType(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntityOperationType>(Tables.OperationTypes)

    val name by Tables.OperationTypes.name
    val description by Tables.OperationTypes.description
}

class EntityOperation(id: EntityID<UUID>) : UUIDEntity(id) {

    companion object : UUIDEntityClass<EntityOperation>(Tables.Operations)

    val sample by EntitySample optionalReferencedOn Tables.Operations.sample
    val operationType by EntityOperationType optionalReferencedOn Tables.Operations.operationType
    val dateTime by Tables.Operations.dateTime
    val worker by EntityWorker optionalReferencedOn Tables.Operations.worker
    val place by EntityPlace optionalReferencedOn Tables.Operations.place
}

class EntityMeasurement(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntityMeasurement>(Tables.Measurements)

    val sample by EntitySample referencedOn Tables.Measurements.sample
    val dateTime by Tables.Measurements.dateTime
    val operator by EntityWorker optionalReferencedOn Tables.Measurements.operator
    val place by EntityPlace optionalReferencedOn Tables.Measurements.place
    val comment by Tables.Measurements.comment
    val conditions by Tables.Measurements.conditions
    val results by Tables.Measurements.results

}