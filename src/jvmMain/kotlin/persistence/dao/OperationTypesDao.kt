package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.OperationType
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import persistence.dto.EntityOperationType
import persistence.dto.Tables
import persistence.toOperationType
import utils.toUUID

class OperationTypesDao : IBaseDao<OperationType> {

    private val table = Tables.OperationTypes


    override suspend fun getByID(id: String): OperationType? {
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
    ): EntitiesList<OperationType> {
        //query all:
        return newSuspendedTransaction {
            val types = EntityOperationType
                .all()
                .map { it.toOperationType() }

            EntitiesList.NotGrouped(types)
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

    override suspend fun update(entity: OperationType) {
        newSuspendedTransaction {
            table.update({ table.id eq entity.id.toUUID() }) {
                it[table.name] = entity.name
                it[table.description] = entity.description
            }
            commit()
        }
    }

    override suspend fun insert(entity: OperationType) {
        newSuspendedTransaction {
            table.insert {
                it[table.id] = entity.id.toUUID()
                it[table.name] = entity.name
                it[table.description] = entity.description
            }
            commit()
        }
    }
}