package persistence.json

import domain.Measurement
import domain.MeasurementResult
import persistence.json.dto.JSONMeasurement
import persistence.json.dto.JSONMeasurementResult
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
        results = this.results.map { it.toJSONMesurementResult() }
    )
}

fun MeasurementResult.toJSONMesurementResult(): JSONMeasurementResult {
    return JSONMeasurementResult(
        parameter = this.parameter.name,
        value = this.value
    )
}
