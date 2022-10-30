package domain

import domain.valueobjects.Factor
import kotlinx.serialization.Serializable
import persistence.export_import.json.serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.*

interface IEntity {
    val id: String
}

/**
 * Entity representing room (a place)
 */
@Serializable
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
@Serializable
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

    fun formatName(): String =
        "${name.firstOrNull()?.plus(". ") ?: ""}${middleName.firstOrNull()?.plus(". ") ?: ""}$surname"
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
    val sampleType: SampleType,
    val sample: Sample? = null,
    val operationType: OperationType? = null,
    val dateTime: LocalDateTime? = null,
    val worker: Worker? = null,
    val place: Place? = null
) : IEntity

/**
 * Entity representing sample type
 */
@Serializable
data class SampleType(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
) : IEntity {
    override fun toString() = name
}

@Serializable
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
@Serializable
data class Parameter(
    override val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val sampleType: SampleType,
    val description: String = "",
    val position: Int? = null,
    val unit: Unit? = null,
    val factor: Factor? = null,
    val norms: List<Norm> = listOf()
) : IEntity {
    override fun toString() = name
}

/**
 * Entity representing [parameter] norms for given [condition]
 */
@Serializable
data class Norm(
    override val id: String = UUID.randomUUID().toString(),
    val condition: String,
    val notLess: Double? = null,
    val notMore: Double? = null,
    val average: Double? = null,
    val sNorm: String? = null // preserved for non-numeric result values
) : IEntity

@Serializable
data class Unit(
    override val id: String = UUID.randomUUID().toString(),
    val unit: String = "",  //A, V, Ohm
    val isMultipliable: Boolean = false //can be prefixed with multiplier: -k, -u, -M, -m, ...
) : IEntity {
    override fun toString(): String = unit
}

@Serializable
data class MeasurementResult(
    val parameter: Parameter,
    val value: Double?,
    val sValue: String? = null // preserved for non-numeric result values
)

/**
 * Entity representing single measurement of the [sample].
 * [results] is a JSON serialized string like array of { parameterID: value}
 * fixme: add [SampleType] here
 */
@Serializable
data class Measurement(
    override val id: String = UUID.randomUUID().toString(),
    val sample: Sample? = null,
    val sampleType: SampleType? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateTime: LocalDateTime? = null,
    val operator: Worker? = null,
    val place: Place? = null,
    val comment: String? = null,
    val conditions: String? = null,
    val results: List<MeasurementResult> = listOf()
) : IEntity