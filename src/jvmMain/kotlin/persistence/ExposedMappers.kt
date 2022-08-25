package persistence

import domain.Parameter
import domain.Sample
import domain.SampleType
import persistence.dto.EntityParameter
import persistence.dto.EntitySample
import persistence.dto.EntitySampleType

fun EntitySampleType.toSampleType(): SampleType {
    return SampleType(
        id = this.id.value.toString(),
        name = this.name,
        description = this.description ?: ""
    )
}

fun EntitySample.toSample(): Sample {
    return Sample(
        id = this.sampleID,
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
        position = this.position
    )
}