package persistence.exposed.dao

import domain.Measurement
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityMeasurement
import persistence.exposed.dto.Tables
import persistence.exposed.toMeasurement
import utils.log
import utils.toUUID

class MeasurementsDao : BaseExposedDao<Measurement, EntityMeasurement, Tables.Measurements>(
    table = Tables.Measurements,
    entityClass = EntityMeasurement
) {
    override fun mapToEntity(expEntity: EntityMeasurement): Measurement = expEntity.toMeasurement()

    override fun updateStatement(entity: Measurement): Tables.Measurements.(UpdateStatement) -> Unit = {
        it[table.sample] = entity.sample?.id?.toUUID()
        it[table.operator] = entity.operator?.id?.toUUID()
        it[table.place] = entity.place?.id?.toUUID()
        it[table.comment] = entity.comment
        it[table.conditions] = entity.conditions
        it[table.dateTime] = entity.dateTime
    }

    override fun insertStatement(entity: Measurement): Tables.Measurements.(InsertStatement<Number>) -> Unit = {
        it[table.sample] = entity.sample?.id?.toUUID()
        it[table.operator] = entity.operator?.id?.toUUID()
        it[table.place] = entity.place?.id?.toUUID()
        it[table.comment] = entity.comment
        it[table.conditions] = entity.conditions
        it[table.dateTime] = entity.dateTime
    }

    override fun Transaction.doAfterUpdate(entity: Measurement) {
        log("updating measurement: $entity")

        //get all parametersID - resultID map for current measurement
        val parametersResultsID =
            Tables.MeasurementResults.slice(Tables.MeasurementResults.id, Tables.MeasurementResults.parameter)
                .select { Tables.MeasurementResults.measurement eq entity.id.toUUID() }
                .associate { it[Tables.MeasurementResults.parameter].value.toString() to it[Tables.MeasurementResults.id].value }


        entity
            .results
            .forEach { mr ->
                val resultID = parametersResultsID[mr.parameter.id]
                log("resultID: $resultID for parameter:${mr.parameter.name}")

                if (resultID != null) {
                    //result exists - update it:
                    log("updating result with id: $resultID")
                    Tables
                        .MeasurementResults
                        .update(where = { Tables.MeasurementResults.id eq resultID }) {
                            it[value] = mr.value
                            it[measurement] = entity.id.toUUID()
                            it[parameter] = mr.parameter.id.toUUID()
                        }
                } else {
                    //result does not exist - insert it:
                    log("inserting new result for parameter: ${mr.parameter.name}")
                    Tables
                        .MeasurementResults
                        .insert {
                            it[value] = mr.value
                            it[measurement] = entity.id.toUUID()
                            it[parameter] = mr.parameter.id.toUUID()
                        }
                }


//                    .update(where = { Tables.MeasurementResults.measurement eq entity.id.toUUID() and (Tables.MeasurementResults.parameter eq mr.parameter.id.toUUID()) }) {
//                        it[value] = mr.value
//                        it[measurement] = entity.id.toUUID()
//                        it[parameter] = mr.parameter.id.toUUID()
//                    }
            }

    }

    override fun Transaction.doAfterInsert(entity: Measurement) {
        entity
            .results
            .forEach { mr ->
                Tables
                    .MeasurementResults
                    .insert {
                        it[value] = mr.value
                        it[measurement] = entity.id.toUUID()
                        it[parameter] = mr.parameter.id.toUUID()
                    }
            }

    }

}