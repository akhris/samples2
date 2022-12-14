package persistence.exposed.dao

import domain.Operation
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityOperation
import persistence.exposed.dto.Tables
import persistence.exposed.toOperation
import utils.toUUID

class OperationsDao : BaseExposedDao<Operation, EntityOperation, Tables.Operations>(
    table = Tables.Operations,
    entityClass = EntityOperation
) {

    override fun insertStatement(entity: Operation): Tables.Operations.(InsertStatement<Number>) -> Unit = {
        it[table.sampleType] = entity.sampleType.id.toUUID()
        it[table.operationType] = entity.operationType?.id?.toUUID()
        it[table.sample] = entity.sample?.id?.toUUID()
        it[table.place] = entity.place?.id?.toUUID()
        it[table.worker] = entity.worker?.id?.toUUID()
        it[table.dateTime] = entity.dateTime
    }

    override fun updateStatement(entity: Operation): Tables.Operations.(UpdateStatement) -> Unit = {
        it[table.sampleType] = entity.sampleType.id.toUUID()
        it[table.operationType] = entity.operationType?.id?.toUUID()
        it[table.sample] = entity.sample?.id?.toUUID()
        it[table.place] = entity.place?.id?.toUUID()
        it[table.worker] = entity.worker?.id?.toUUID()
        it[table.dateTime] = entity.dateTime
    }

    override fun mapToEntity(expEntity: EntityOperation): Operation = expEntity.toOperation()
}