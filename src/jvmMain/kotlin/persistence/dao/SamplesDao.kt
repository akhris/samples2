package persistence.dao

import domain.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import persistence.dto.EntitySample
import persistence.dto.Tables
import persistence.toSample
import utils.log
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
        log("query with searchSpec: $searchSpec")

        //query all:
        return newSuspendedTransaction {


            val query = table.selectAll()
//
            (searchSpec as? Specification.Search)?.let {
                query.addSearching(it)
            }

            val types =
                EntitySample
                    .wrapRows(query)
                    .map { it.toSample() }
//                .all()
//                .map { it.toSample() }

            EntitiesList.NotGrouped(types)
        }
    }

    private fun Query.addSearching(searchSpec: Specification.Search) {
        if (searchSpec.searchString.isBlank())
            return

        table.columns.forEach { c ->
            //search if it's text
            (c as? Column<String>)?.let {
                orWhere { it.lowerCase() like "%${searchSpec.searchString}%" }
            }
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
            table.update({ table.id eq entity.id.toUUID() }) {
                it[sampleID] = entity.identifier
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
                it[id] = entity.id.toUUID()
                it[sampleID] = entity.identifier
                it[comment] = entity.comment
                it[orderID] = entity.orderID
                it[description] = entity.description
                it[type] = entity.type.id.toUUID()
            }
            commit()
        }
    }
}