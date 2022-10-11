package persistence.json.dto

import kotlinx.serialization.Serializable

@Serializable
data class JSONMeasurement(
    val sampleID: String,
    val dateTime: String,
    val operator: String,
    val comment: String,
    val conditions: String,
    val results: List<JSONResult>
)

@Serializable
data class JSONResult(
    val parameterID: String,
    val value: String,
    val unit: String
)