package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Parameter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import persistence.dto.EntityParameter
import persistence.dto.Tables
import persistence.toParameter
import utils.toUUID

class ParametersDao : IBaseDao<Parameter> {

    private val table = Tables.Parameters

    override suspend fun getByID(id: String): Parameter? {
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
    ): EntitiesList<Parameter> {
        //query all:
        return newSuspendedTransaction {
            val parameters = EntityParameter
                .all()
                .map { it.toParameter() }
            EntitiesList.NotGrouped(parameters)
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

    override suspend fun update(entity: Parameter) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.update({ table.id eq entity.id.toUUID() }) {
                it[name] = entity.name
                it[description] = entity.description
                it[position] = entity.position
                it[sampleType] = entity.sampleType.id.toUUID()
            }
            commit()
        }
    }

    override suspend fun insert(entity: Parameter) {
        newSuspendedTransaction {
            table.insert {
                it[id] = entity.id.toUUID()
                it[name] = entity.name
                it[description] = entity.description
                it[position] = entity.position
                it[sampleType] = entity.sampleType.id.toUUID()
            }
            commit()
        }
    }
}