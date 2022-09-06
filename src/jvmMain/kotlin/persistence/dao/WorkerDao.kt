package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Worker
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import persistence.dto.EntityWorker
import persistence.dto.Tables
import persistence.toWorker
import utils.toUUID

class WorkerDao : IBaseDao<Worker> {

    private val table = Tables.Workers


    override suspend fun getByID(id: String): Worker? {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: String) {
        newSuspendedTransaction {
            table.deleteWhere { table.id eq id.toUUID() }
            commit()
        }
    }

    override suspend fun query(
        filterSpec: ISpecification?,
        sortingSpec: ISpecification?,
        pagingSpec: ISpecification?,
        searchSpec: ISpecification?,
        groupingSpec: ISpecification?
    ): EntitiesList<Worker> {
        return newSuspendedTransaction {
            val workers = EntityWorker
                .all()
                .map { it.toWorker() }
            EntitiesList.NotGrouped(workers)
        }
    }

    override suspend fun getItemsCount(
        filterSpec: ISpecification?,
        sortingSpec: ISpecification?,
        pagingSpec: ISpecification?,
        searchSpec: ISpecification?,
        groupingSpec: ISpecification?
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Worker) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.update({ table.id eq entity.id.toUUID() }) {
                it[table.name] = entity.name
                it[table.surname] = entity.surname
                it[table.middleName] = entity.middleName
                it[table.email] = entity.email
                it[table.phoneNumber] = entity.phoneNumber
                it[table.room] = entity.place?.id?.toUUID()
            }
            commit()
        }
    }

    override suspend fun insert(entity: Worker) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.insert {
                it[table.id] = entity.id.toUUID()
                it[table.name] = entity.name
                it[table.surname] = entity.surname
                it[table.middleName] = entity.middleName
                it[table.email] = entity.email
                it[table.phoneNumber] = entity.phoneNumber
                it[table.room] = entity.place?.id?.toUUID()
            }
            commit()
        }
    }
}