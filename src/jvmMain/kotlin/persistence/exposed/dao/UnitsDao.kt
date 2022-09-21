package persistence.exposed.dao

import domain.Unit
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityUnit
import persistence.exposed.dto.Tables
import persistence.toUnit

class UnitsDao : BaseExposedDao<Unit, EntityUnit, Tables.Units>(
    table = Tables.Units,
    entityClass = EntityUnit
) {
    override fun mapToEntity(expEntity: EntityUnit): Unit = expEntity.toUnit()

    override fun updateStatement(entity: Unit): Tables.Units.(UpdateStatement) -> kotlin.Unit = {
        it[table.unit] = entity.unit
        it[table.isMultipliable] = entity.isMultipliable
    }

    override fun insertStatement(entity: Unit): Tables.Units.(InsertStatement<Number>) -> kotlin.Unit = {
        it[table.unit] = entity.unit
        it[table.isMultipliable] = entity.isMultipliable
    }


}