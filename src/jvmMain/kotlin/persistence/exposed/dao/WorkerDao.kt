package persistence.exposed.dao

import domain.Worker
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityWorker
import persistence.exposed.dto.Tables
import persistence.exposed.toWorker
import utils.toUUID

class WorkerDao : BaseExposedDao<Worker, EntityWorker, Tables.Workers>(
    table = Tables.Workers,
    entityClass = EntityWorker
) {

    override fun mapToEntity(expEntity: EntityWorker): Worker = expEntity.toWorker()

    override fun updateStatement(entity: Worker): Tables.Workers.(UpdateStatement) -> Unit = {
        it[table.name] = entity.name
        it[table.surname] = entity.surname
        it[table.middleName] = entity.middleName
        it[table.email] = entity.email
        it[table.phoneNumber] = entity.phoneNumber
        it[table.room] = entity.place?.id?.toUUID()
    }

    override fun insertStatement(entity: Worker): Tables.Workers.(InsertStatement<Number>) -> Unit = {
        it[table.name] = entity.name
        it[table.surname] = entity.surname
        it[table.middleName] = entity.middleName
        it[table.email] = entity.email
        it[table.phoneNumber] = entity.phoneNumber
        it[table.room] = entity.place?.id?.toUUID()
    }

}