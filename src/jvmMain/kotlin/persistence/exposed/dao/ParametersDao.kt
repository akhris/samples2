package persistence.exposed.dao

import domain.EntitiesList
import domain.ISpecification
import domain.Parameter
import domain.Specification
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import persistence.exposed.dto.EntityParameter
import persistence.exposed.dto.Tables
import persistence.exposed.toParameter
import ui.components.tables.ColumnId
import utils.replaceOrAdd
import utils.toUUID

class ParametersDao : BaseExposedDao<Parameter, EntityParameter, Tables.Parameters>(
    table = Tables.Parameters,
    entityClass = EntityParameter
) {

    override fun insertStatement(entity: Parameter): Tables.Parameters.(InsertStatement<Number>) -> Unit = {
        it[name] = entity.name
        it[description] = entity.description
        it[position] = entity.position
            ?: ((table.slice(table.position).selectAll().maxOfOrNull { row -> row[table.position] ?: 0 } ?: 0) + 1)
        it[sampleType] = entity.sampleType.id.toUUID()
        it[unit] = entity.unit?.id?.toUUID()
        it[factor] = entity.factor?.factor
    }

    override fun updateStatement(entity: Parameter): Tables.Parameters.(UpdateStatement) -> Unit = {
        it[name] = entity.name
        it[description] = entity.description
        it[position] = entity.position
        it[sampleType] = entity.sampleType.id.toUUID()
        it[unit] = entity.unit?.id?.toUUID()
        it[factor] = entity.factor?.factor
    }

    override fun mapToEntity(expEntity: EntityParameter): Parameter = expEntity.toParameter()

    override suspend fun query(specs: List<ISpecification>): EntitiesList<Parameter> {
        return super.query(
            specs.replaceOrAdd(
                Specification.Sorted(
                    ColumnId(
                        Tables.Parameters.position.name,
                        "Позиция"
                    )
                )
            ) { oldSpec -> oldSpec is Specification.Sorted })
    }

}