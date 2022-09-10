package persistence.exposed.dao

import domain.Measurement
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.BatchUpdateStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update
import persistence.exposed.dto.EntityMeasurement
import persistence.exposed.dto.EntityMeasurementResult
import persistence.exposed.dto.Tables
import persistence.toMeasurement
import utils.toUUID

class MeasurementsDao : BaseExposedDao<Measurement, EntityMeasurement, Tables.Measurements>(
    table = Tables.Measurements,
    entityClass = EntityMeasurement
) {
    override fun mapToEntity(expEntity: EntityMeasurement): Measurement = expEntity.toMeasurement()

    override fun updateStatement(entity: Measurement): Tables.Measurements.(UpdateStatement) -> Unit = {
        it[table.sample] = entity.sample.id.toUUID()
        it[table.operator] = entity.operator?.id?.toUUID()
        it[table.place] = entity.place?.id?.toUUID()
        it[table.comment] = entity.comment
        it[table.conditions] = entity.conditions
        it[table.dateTime] = entity.dateTime
    }

    override fun insertStatement(entity: Measurement): Tables.Measurements.(InsertStatement<Number>) -> Unit = {
        it[table.sample] = entity.sample.id.toUUID()
        it[table.operator] = entity.operator?.id?.toUUID()
        it[table.place] = entity.place?.id?.toUUID()
        it[table.comment] = entity.comment
        it[table.conditions] = entity.conditions
        it[table.dateTime] = entity.dateTime
    }

    override fun Transaction.doAfterUpdate(entity: Measurement) {
        entity
            .results
            .forEach { mr ->
                Tables
                    .MeasurementResults
                    .update(where = { Tables.MeasurementResults.measurement eq entity.id.toUUID() and (Tables.MeasurementResults.parameter eq mr.parameter.id.toUUID()) }) {
                        it[value] = mr.value
                        it[unit] = mr.unit
                    }
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
                        it[unit] = mr.unit
                        it[measurement] = entity.id.toUUID()
                        it[parameter] = mr.parameter.id.toUUID()
                    }
            }

    }

}