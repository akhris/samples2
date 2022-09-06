package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.SampleType
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import persistence.dto.EntitySampleType
import persistence.dto.Tables
import persistence.toSampleType
import utils.toUUID

class SampleTypesDao : IBaseDao<SampleType> {

    private val table = Tables.SampleTypes

    override suspend fun getByID(id: String): SampleType? {
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
    ): EntitiesList<SampleType> {
        //query all:
        return newSuspendedTransaction {
            val types = EntitySampleType
                .all()
                .map { it.toSampleType() }

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

    override suspend fun update(entity: SampleType) {
        newSuspendedTransaction {
            table.update({ table.id eq entity.id.toUUID() }) {
                it[name] = entity.name
                it[description] = entity.description
            }
            commit()
        }
    }

    override suspend fun insert(entity: SampleType) {
        newSuspendedTransaction {
            table.insert {
                it[id] = entity.id.toUUID()
                it[name] = entity.name
                it[description] = entity.description
            }
            commit()
        }
    }

}