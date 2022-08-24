package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Sample
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import persistence.dto.EntitySample
import persistence.dto.Tables
import persistence.toSample
import utils.toUUID

class SamplesDao : IBaseDao<Sample> {

    private val table = Tables.Samples
    override suspend fun getByID(id: String): Sample? {
        return newSuspendedTransaction {
            EntitySample
                .find { table.sampleID eq id }
                .singleOrNull()
                ?.toSample()
        }
    }

    override suspend fun removeById(id: String) {
        newSuspendedTransaction {
            table.deleteWhere { table.sampleID eq id }
            commit()
        }
    }

    override suspend fun query(
        filterSpec: ISpecification?,
        sortingSpec: ISpecification?,
        pagingSpec: ISpecification?,
        searchSpec: ISpecification?,
        groupingSpec: ISpecification?
    ): EntitiesList<Sample> {
        //query all:
        return newSuspendedTransaction {
            val types = EntitySample
                .all()
                .map { it.toSample() }

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

    override suspend fun update(entity: Sample) {
        newSuspendedTransaction {
            table.update({ table.sampleID eq entity.id }) {
                it[sampleID] = entity.id
                it[comment] = entity.comment
                it[orderID] = entity.orderID
                it[description] = entity.description
                it[type] = entity.type.id.toUUID()
            }
            commit()
        }
    }

    override suspend fun insert(entity: Sample) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.insert {
                it[sampleID] = entity.id
                it[comment] = entity.comment
                it[orderID] = entity.orderID
                it[description] = entity.description
                it[type] = entity.type.id.toUUID()
            }
            commit()
        }
    }
}