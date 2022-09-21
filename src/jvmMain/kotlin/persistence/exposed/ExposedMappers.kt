package persistence

import domain.*
import domain.Unit
import persistence.exposed.dto.*

fun EntitySampleType.toSampleType(): SampleType {
    return SampleType(
        id = this.id.value.toString(),
        name = this.name,
        description = this.description ?: ""
    )
}

fun EntitySample.toSample(): Sample {
    return Sample(
        id = this.id.value.toString(),
        identifier = this.sampleID,
        description = this.description,
        orderID = this.orderID,
        comment = this.comment,
        type = this.type.toSampleType()
    )
}

fun EntityParameter.toParameter(): Parameter {
    return Parameter(
        id = this.id.value.toString(),
        name = this.name,
        sampleType = this.sampleType.toSampleType(),
        description = this.description ?: "",
        position = this.position,
        unit = this.unit?.toUnit(),
        factor = this.factor
    )
}

fun EntityOperationType.toOperationType(): OperationType {
    return OperationType(id = this.id.value.toString(), name = this.name, description = this.description ?: "")
}

fun EntityPlace.toPlace(): Place {
    return Place(id = this.id.value.toString(), roomNumber = this.roomNumber, description = this.description ?: "")
}

fun EntityWorker.toWorker(): Worker {
    return Worker(
        id = this.id.value.toString(),
        name = this.name,
        middleName = this.middleName,
        surname = this.surname,
        place = this.room?.toPlace(),
        phoneNumber = this.phoneNumber,
        email = this.email
    )
}

fun EntityOperation.toOperation(): Operation {
    return Operation(
        id = this.id.value.toString(),
        sample = this.sample?.toSample(),
        operationType = this.operationType?.toOperationType(),
        dateTime = this.dateTime,
        worker = this.worker?.toWorker(),
        place = this.place?.toPlace()
    )
}

fun EntityMeasurement.toMeasurement(): Measurement {
    return Measurement(
        id = this.id.value.toString(),
        sample = this.sample?.toSample(),
        operator = this.operator?.toWorker(),
        place = this.place?.toPlace(),
        dateTime = this.dateTime,
        comment = this.comment,
        conditions = this.conditions,
        results = this.results.map { it.toMeasurementResult() }
    )
}

fun EntityMeasurementResult.toMeasurementResult(): MeasurementResult {
    return MeasurementResult(
        parameter = this.parameter.toParameter(),
        value = this.value ?: ""
    )
}

fun EntityUnit.toUnit(): Unit {
    return Unit(id = this.id.value.toString(), unit = this.unit, isMultipliable = this.isMultipliable)
}