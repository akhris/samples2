package persistence.export_import.json.dto

import kotlinx.serialization.Serializable


@Serializable
data class JSONMeasurement(
    val sample: String?,
    val type: String?,
    val operator: String?,
    val place: String?,
    val dateTime: String?,
    val comment: String?,
    val conditions: String?,
    val results: List<JSONMeasurementResult>
)

@Serializable
data class JSONMeasurementResult(
    val parameter: String,
    val value: Double?
)
