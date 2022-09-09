package persistence.exposed.dao

import domain.Sample
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntitySample
import persistence.exposed.dto.Tables
import persistence.toSample
import utils.toUUID

class SamplesDao : BaseExposedDao<Sample, EntitySample, Tables.Samples>(
    table = Tables.Samples,
    entityClass = EntitySample
) {

    override fun mapToEntity(expEntity: EntitySample): Sample = expEntity.toSample()

    override fun updateStatement(entity: Sample): Tables.Samples.(UpdateStatement) -> Unit = {
        it[sampleID] = entity.identifier
        it[comment] = entity.comment
        it[orderID] = entity.orderID
        it[description] = entity.description
        it[type] = entity.type.id.toUUID()
    }

    override fun insertStatement(entity: Sample): Tables.Samples.(InsertStatement<Number>) -> Unit = {
        it[sampleID] = entity.identifier
        it[comment] = entity.comment
        it[orderID] = entity.orderID
        it[description] = entity.description
        it[type] = entity.type.id.toUUID()
    }

}