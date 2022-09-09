package persistence.exposed.dao

import domain.OperationType
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityOperationType
import persistence.exposed.dto.Tables
import persistence.toOperationType

class OperationTypesDao : BaseExposedDao<OperationType, EntityOperationType, Tables.OperationTypes>(
    table = Tables.OperationTypes,
    entityClass = EntityOperationType
) {

    override fun insertStatement(entity: OperationType): Tables.OperationTypes.(InsertStatement<Number>) -> Unit = {
        it[table.name] = entity.name
        it[table.description] = entity.description
    }

    override fun updateStatement(entity: OperationType): Tables.OperationTypes.(UpdateStatement) -> Unit = {
        it[table.name] = entity.name
        it[table.description] = entity.description
    }

    override fun mapToEntity(expEntity: EntityOperationType): OperationType = expEntity.toOperationType()
}