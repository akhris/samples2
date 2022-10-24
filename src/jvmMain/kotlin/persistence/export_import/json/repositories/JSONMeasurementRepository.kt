package persistence.export_import.json.repositories

import domain.IExportImportRepository
import domain.Measurement
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import persistence.export_import.json.dto.JSONMeasurement
import persistence.export_import.json.toJSONMeasurement
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText

class JSONMeasurementRepository(private val format: Json) : IExportImportRepository<Measurement, JSONMeasurement> {

    override suspend fun export(filePath: String, entities: List<Measurement>) {
        val json = format.encodeToString(entities.map { it.toJSONMeasurement() })
        Path(filePath).writeText(json)
    }

    override suspend fun import(filePath: String): List<JSONMeasurement> {
        val jsonText = Path(filePath).readText()
        return format.decodeFromString(string = jsonText)
    }
}