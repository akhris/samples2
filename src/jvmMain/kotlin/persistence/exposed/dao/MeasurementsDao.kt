package persistence.exposed.dao

import domain.Measurement
import org.jetbrains.exposed.sql.*
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
        entity
            .results
            .forEach { mr ->
                log("inserting $mr")
                Tables
                    .MeasurementResults
                    .insert {
                        it[value] = mr.value
                        it[measurement] = entity.id.toUUID()
                        it[parameter] = mr.parameter.id.toUUID()
                    }
//                    .update(where = { Tables.MeasurementResults.measurement eq entity.id.toUUID() and (Tables.MeasurementResults.parameter eq mr.parameter.id.toUUID()) }) {
//
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