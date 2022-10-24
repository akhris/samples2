package persistence.export_import.json

import domain.Measurement
import domain.MeasurementResult
import persistence.export_import.json.dto.JSONMeasurement
import persistence.export_import.json.dto.JSONMeasurementResult
import java.time.format.DateTimeFormatter


fun Measurement.toJSONMeasurement(): JSONMeasurement {
    return JSONMeasurement(
        sample = this.sample?.identifier,
        type = this.sampleType?.name,
        operator = this.operator?.surname,
        dateTime = this.dateTime?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
        place = this.place?.roomNumber,
        comment = this.comment,
        conditions = this.conditions,
        results = this.results.map { it.toJSONMeasurementResult() }
    )
}

fun MeasurementResult.toJSONMeasurementResult(): JSONMeasurementResult {
    return JSONMeasurementResult(
        parameter = this.parameter.name,
        value = this.value
    )
}
