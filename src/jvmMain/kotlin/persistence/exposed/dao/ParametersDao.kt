package persistence.exposed.dao

import domain.EntitiesList
import domain.ISpecification
import domain.Parameter
import domain.Specification
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    override fun insertStatement(entity: Parameter): Tables.Parameters.(InsertStatement<*>) -> Unit = {
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

    override fun Transaction.doAfterUpdate(entity: Parameter) {
        val normsIDs =
            Tables
                .Norms
                .slice(Tables.Norms.id)
                .select { Tables.Norms.parameter eq entity.id.toUUID() }
                .map { it[Tables.Norms.id].value.toString() }

        entity
            .norms
            .forEach { norm ->
                if (norm.id in normsIDs) {
                    //norm is already in database:
                    Tables
                        .Norms
                        .update(where = { Tables.Norms.id eq norm.id.toUUID() }) {
                            it[id] = norm.id.toUUID()
                            it[parameter] = entity.id.toUUID()
                            it[condition] = norm.condition
                            //todo add norm values here
                        }
                } else {
                    //norm is not in database -> insert one:
                    Tables
                        .Norms
                        .insert {
                            it[id] = norm.id.toUUID()
                            it[parameter] = entity.id.toUUID()
                            it[condition] = norm.condition
                            //todo add norm values here
                        }
                }
            }
    }

    override fun Transaction.doAfterInsert(entity: Parameter) {
        Tables
            .Norms
            .batchInsert(entity.norms) { norm ->
                this[Tables.Norms.id] = norm.id.toUUID()
                this[Tables.Norms.parameter] = entity.id.toUUID()
                this[Tables.Norms.condition] = norm.condition
                //todo add norm values here
            }
    }

}