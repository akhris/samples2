package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Operation
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import persistence.dto.EntityOperation
import persistence.dto.Tables
import persistence.toOperation
import utils.toUUID

class OperationsDao : IBaseDao<Operation> {
    private val table = Tables.Operations

    override suspend fun getByID(id: String): Operation? {
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
    ): EntitiesList<Operation> {
        //query all:
        return newSuspendedTransaction {
            val operations = EntityOperation
                .all()
                .map { it.toOperation() }
            EntitiesList.NotGrouped(operations)
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

    override suspend fun update(entity: Operation) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.update({ table.id eq entity.id.toUUID() }) {
                it[table.operationType] = entity.operationType.id.toUUID()
                it[table.sample] = entity.sample.id.toUUID()
                it[table.place] = entity.place.id.toUUID()
                it[table.worker] = entity.worker.id.toUUID()
                it[table.dateTime] = entity.dateTime
            }
            commit()
        }
    }

    override suspend fun insert(entity: Operation) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.insert {
                it[table.operationType] = entity.operationType.id.toUUID()
                it[table.sample] = entity.sample.id.toUUID()
                it[table.place] = entity.place.id.toUUID()
                it[table.worker] = entity.worker.id.toUUID()
                it[table.dateTime] = entity.dateTime
            }
            commit()
        }
    }
}