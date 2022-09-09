package persistence.exposed.dao

import domain.Place
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityPlace
import persistence.exposed.dto.Tables
import persistence.toPlace

class PlacesDao : BaseExposedDao<Place, EntityPlace, Tables.Places>(
    table = Tables.Places,
    entityClass = EntityPlace
) {

    override fun mapToEntity(expEntity: EntityPlace): Place = expEntity.toPlace()

    override fun updateStatement(entity: Place): Tables.Places.(UpdateStatement) -> Unit = {
        it[table.name] = entity.name
        it[table.description] = entity.description
        it[table.roomNumber] = entity.roomNumber
    }

    override fun insertStatement(entity: Place): Tables.Places.(InsertStatement<Number>) -> Unit = {
        it[table.name] = entity.name
        it[table.description] = entity.description
        it[table.roomNumber] = entity.roomNumber
    }

}