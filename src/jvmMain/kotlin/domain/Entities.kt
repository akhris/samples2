package domain

import java.util.*

/**
 * Entity representing room (a place)
 */
data class Room(
    val roomID: String = UUID.randomUUID().toString(),
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
    val room: Room? = null,
    val phoneNumber: String = "",
    val email: String = ""
)

/**
 * Entity representing operation
 */
data class Operation(
    val operationID: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
)

/**
 * Entity representing sample type
 */
data class SampleType(
    val typeID: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = ""
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
    val sampleType: SampleType,
    val parameter: Parameter,
    val condition: Condition
)