package domain

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

interface IEntity {
    val id: String
}

/**
 * Entity representing room (a place)
 */
data class Place(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val roomNumber: String = "",
    val description: String = ""
) : IEntity {
    override fun toString() = roomNumber
}

/**
 * Entity representing a person
 */
data class Worker(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val middleName: String = "",
    val surname: String = "",
    val place: Place? = null,
    val phoneNumber: String = "",
    val email: String = ""
) : IEntity {
    override fun toString() = "$name $surname"
}


/**
 * Entity representing operation type
 */
data class OperationType(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
) : IEntity {
    override fun toString() = name
}

/**
 * Entity representing actual operation
 */
data class Operation(
    override val id: String = UUID.randomUUID().toString(),
    val sample: Sample? = null,
    val operationType: OperationType? = null,
    val dateTime: LocalDateTime? = null,
    val worker: Worker? = null,
    val place: Place? = null
) : IEntity

/**
 * Entity representing sample type
 */
data class SampleType(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
) : IEntity {
    override fun toString() = name
}

data class Sample(
    override val id: String = UUID.randomUUID().toString(), //id for database
    val identifier: String? = null, //id written on the sample
    val type: SampleType,
    val description: String? = null,
    val comment: String? = null,
    val orderID: String? = null
) : IEntity {
    override fun toString() = identifier ?: "<нет ID>"
}

/**
 * Entity representing sample parameter
 */
data class Parameter(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val sampleType: SampleType,
    val description: String = "",
    val position: Int? = null
) : IEntity {
    override fun toString() = name
}

/**
 * Entity representing certain conditions at which measurements take place.
 * e.g.: temperature=85C, pressure = 3atm, e.t.c
 */
data class Condition(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
) : IEntity {
    override fun toString() = name
}

/**
 * Entity representing [parameter] norms for given [condition]
 */
data class Norm(
    override val id: String = UUID.randomUUID().toString(),
    val parameter: Parameter,
    val condition: Condition
    // TODO: add values range for norm (min, max, avg)
) : IEntity


data class MeasurementResult(
    val parameter: Parameter,
    val value: String,
    val unit: String
)

/**
 * Entity representing single measurement of the [sample].
 * [results] is a JSON serialized string like array of { parameterID: value}
 */
data class Measurement(
    override val id: String = UUID.randomUUID().toString(),
    val sample: Sample? = null,
    val dateTime: LocalDateTime? = null,
    val operator: Worker? = null,
    val place: Place? = null,
    val comment: String? = null,
    val conditions: String? = null,
    val results: List<MeasurementResult> = listOf()
) : IEntity