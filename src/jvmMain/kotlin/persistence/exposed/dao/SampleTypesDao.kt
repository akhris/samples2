package persistence.exposed.dao

import domain.SampleType
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntitySampleType
import persistence.exposed.dto.Tables
import persistence.toSampleType

class SampleTypesDao : BaseExposedDao<SampleType, EntitySampleType, Tables.SampleTypes>(
    table = Tables.SampleTypes,
    entityClass = EntitySampleType
) {

    override fun mapToEntity(expEntity: EntitySampleType): SampleType = expEntity.toSampleType()

    override fun updateStatement(entity: SampleType): Tables.SampleTypes.(UpdateStatement) -> Unit = {
        it[name] = entity.name
        it[description] = entity.description
    }

    override fun insertStatement(entity: SampleType): Tables.SampleTypes.(InsertStatement<Number>) -> Unit = {
        it[name] = entity.name
        it[description] = entity.description
    }

}