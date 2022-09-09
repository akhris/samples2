package persistence.exposed.dao

import domain.Parameter
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityParameter
import persistence.exposed.dto.Tables
import persistence.toParameter
import utils.toUUID

class ParametersDao : BaseExposedDao<Parameter, EntityParameter, Tables.Parameters>(
    table = Tables.Parameters,
    entityClass = EntityParameter
) {

    override fun insertStatement(entity: Parameter): Tables.Parameters.(InsertStatement<Number>) -> Unit = {
        it[name] = entity.name
        it[description] = entity.description
        it[position] = entity.position
        it[sampleType] = entity.sampleType.id.toUUID()
    }

    override fun updateStatement(entity: Parameter): Tables.Parameters.(UpdateStatement) -> Unit = {
        it[name] = entity.name
        it[description] = entity.description
        it[position] = entity.position
        it[sampleType] = entity.sampleType.id.toUUID()
    }

    override fun mapToEntity(expEntity: EntityParameter): Parameter = expEntity.toParameter()

}