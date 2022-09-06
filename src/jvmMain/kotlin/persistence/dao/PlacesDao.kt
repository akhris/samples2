package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Place
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import persistence.dto.EntityPlace
import persistence.dto.Tables
import persistence.toPlace
import utils.toUUID

class PlacesDao : IBaseDao<Place> {

    private val table = Tables.Places

    override suspend fun getByID(id: String): Place? {
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
    ): EntitiesList<Place> {
        return newSuspendedTransaction {
            val places = EntityPlace
                .all()
                .map { it.toPlace() }
            EntitiesList.NotGrouped(places)
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

    override suspend fun update(entity: Place) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.update({ table.id eq entity.id.toUUID() }) {
                it[table.name] = entity.name
                it[table.description] = entity.description
                it[table.roomNumber] = entity.roomNumber
            }
            commit()
        }
    }

    override suspend fun insert(entity: Place) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.insert {
                it[table.id] = entity.id.toUUID()
                it[table.name] = entity.name
                it[table.description] = entity.description
                it[table.roomNumber] = entity.roomNumber
            }
            commit()
        }
    }
}