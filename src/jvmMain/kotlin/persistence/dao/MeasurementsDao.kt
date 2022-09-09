package persistence.dao

import domain.Measurement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.dto.EntityMeasurement
import persistence.dto.Tables
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
        it[table.results] = entity.results
    }

    override fun insertStatement(entity: Measurement): Tables.Measurements.(InsertStatement<Number>) -> Unit = {
        it[table.sample] = entity.sample.id.toUUID()
        it[table.operator] = entity.operator?.id?.toUUID()
        it[table.place] = entity.place?.id?.toUUID()
        it[table.comment] = entity.comment
        it[table.conditions] = entity.conditions
        it[table.dateTime] = entity.dateTime
        it[table.results] = entity.results
    }

}