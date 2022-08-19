package domain

import java.time.LocalDateTime
import java.util.*

/**
 * Entity representing room (a place)
 */
data class Place(
    val placeID: String = UUID.randomUUID().toString(),
    val roomNumber: String = "",
    val description: String = ""
)

/**
 * Entity representing a person
 */
data class Worker(
    val workerID: String = UUID.randomUUID().toString(),
    val name: String = "",
    val middleName: String = "",
    val surname: String = "",
    val place: Place? = null,
    val phoneNumber: String = "",
    val email: String = ""
)

/**
 * Entity representing operation type
 */
data class OperationType(
    val operationID: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
)

/**
 * Entity representing actual operation
 */
data class Operation(
    val sample: Sample,
    val operationType: OperationType,
    val dateTime: LocalDateTime?,
    val worker: Worker,
    val place: Place
)

/**
 * Entity representing sample type
 */
data class SampleType(
    val typeID: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
)

data class Sample(
    val sampleID: String,
    val type: SampleType,
    val description: String
)

/**
 * Entity representing sample parameter
 *
 * [parameterID] here is human-readable id which makes a composite key with [sampleType]
 *
 * there must be no two equal parameterIDs for a sampleType.
 */
data class Parameter(
    val parameterID: String = "",
    val sampleType: SampleType,
    val description: String = ""
)

/**
 * Entity representing certain conditions at which measurements take place.
 * e.g.: temperature=85C, pressure = 3atm, e.t.c
 */
data class Condition(
    val conditionID: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
)

/**
 * Entity representing [parameter] norms for given [condition]
 */
data class Norm(
    val normID: String = UUID.randomUUID().toString(),
    val parameter: Parameter,
    val condition: Condition
    // TODO: add values range for norm (min, max, avg)
)


/**
 * Entity representing single measurement of the [sample].
 * [results] is a JSON serialized string like array of { parameterID: value}
 */
data class Measurement(
    val sample: Sample,
    val results: String
)