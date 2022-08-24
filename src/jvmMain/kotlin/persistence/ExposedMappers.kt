package persistence

import domain.Sample
import domain.SampleType
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