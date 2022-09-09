package persistence.exposed.dao

import domain.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import utils.log
import utils.toUUID

abstract class BaseExposedDao<ENTITY : IEntity, EXP_ENTITY : UUIDEntity, TABLE : UUIDTable>(
    protected val table: TABLE,
    private val entityClass: UUIDEntityClass<EXP_ENTITY>
) :
    IBaseDao<ENTITY> {

    private val withCommit: Boolean = false

    abstract fun mapToEntity(expEntity: EXP_ENTITY): ENTITY

    protected abstract fun updateStatement(entity: ENTITY): TABLE.(UpdateStatement) -> Unit
    protected abstract fun insertStatement(entity: ENTITY): TABLE.(InsertStatement<Number>) -> Unit

    protected open fun Transaction.doAfterUpdate(entity: ENTITY) {}
    protected open fun Transaction.doAfterInsert(entity: ENTITY) {}

    override suspend fun query(specs: List<ISpecification>): EntitiesList<ENTITY> {
        return newSuspendedTransaction {

            val withGrouping = specs.filterIsInstance<Specification.Grouped>().isNotEmpty()

            if (withGrouping) {
                getGroupedItems(specs)
            } else {
                val query = getNotGroupedQuery(specs)
                EntitiesList.NotGrouped(
                    entityClass
                        .wrapRows(query)
                        .map { mapToEntity(it) }
                )
            }

        }
    }

    override suspend fun getItemsCount(specs: List<ISpecification>): Long {
        return newSuspendedTransaction {
            val withGrouping = specs.filterIsInstance<Specification.Grouped>().isNotEmpty()
            if (withGrouping) {
                getGroupsCount(specs)
            } else {
                getNotGroupedQuery(specs).count()
            }
        }
    }

    override suspend fun getByID(id: String): ENTITY? {
        return newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            try {
                mapToEntity(entityClass.get(id = id.toUUID()))
            } catch (e: Exception) {
                log(e.localizedMessage)
                null
            }
        }
    }

    override suspend fun update(entity: ENTITY) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.update(where = { table.id eq entity.id.toUUID() }, body = updateStatement(entity))
            doAfterUpdate(entity)
            if (withCommit)
                commit()
        }
    }

    override suspend fun insert(entity: ENTITY) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.insert { statement ->
                statement[id] = entity.id.toUUID()
                insertStatement(entity).invoke(this, statement)
            }
            doAfterInsert(entity)
            if (withCommit)
                commit()
        }
    }


    override suspend fun removeById(id: String) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.deleteWhere { table.id eq id.toUUID() }
            if (withCommit)
                commit()
        }
    }


    private fun getNotGroupedQuery(specs: List<ISpecification>): Query {
        val query = table.selectAll()
        (specs.filterIsInstance<Specification.Search>().firstOrNull())?.let {
            query.addSearching(it)
        }
        (specs.filterIsInstance<Specification.Filtered>().firstOrNull())?.let {
            //add filtering
        }
        (specs.filterIsInstance<Specification.Sorted>().firstOrNull())?.let {
            //add sorting
            query.addSorting(it)
        }
        (specs.filterIsInstance<Specification.Paginated>().firstOrNull())?.let {
            query.addPaging(it)
        }
        return query
    }


    private fun getGroupedItems(specs: List<ISpecification>): EntitiesList.Grouped<ENTITY> {
        // get entity column ID to group by from spec:
        val groupedBy = (specs.filterIsInstance<Specification.Grouped>().firstOrNull())?.groupingSpec?.columnId
            ?: return EntitiesList.Grouped(listOf())

        val column =
            (table.columns.find { it.name == groupedBy.key } as? Column<Any>) ?: return EntitiesList.Grouped(listOf())

        val groupsKeys =
            table
                .slice(column)
                .selectAll()
                .withDistinct(true)
                .map { it[column] }
                .filterNotNull()

        // for each value - get corresponding items:
        val groupedItems =
            groupsKeys
                .map { key ->
                    key to table
                        .select { column eq key }
                        .map { mapToEntity(entityClass.wrapRow(it)) }
                }
                .map {
                    val keyName = getNameFromForeignTable(column, it.first)?.toString()
                    GroupedItem(
                        groupID = GroupID(
                            categoryName = column.name,
                            key = it.first,
                            keyName = keyName
                        ),
                        items = it.second
                    )
                }
        return EntitiesList.Grouped(groupedItems)
    }

    private fun getGroupsCount(specs: List<ISpecification>): Long {
        // get entity column ID to group by from spec:
        val groupedBy = (specs.filterIsInstance<Specification.Grouped>().firstOrNull())?.groupingSpec?.columnId
            ?: return 0L

        val column =
            (table.columns.find { it.name == groupedBy.key } as? Column<Any>) ?: return 0L

        return table
            .slice(column)
            .selectAll()
            .withDistinct(true)
            .count()
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

    private fun Query.addPaging(pagingSpec: Specification.Paginated) {
        limit(n = pagingSpec.itemsPerPage.toInt(), offset = pagingSpec.itemsPerPage * (pagingSpec.pageNumber - 1))
    }

    private fun Query.addSorting(sortingSpec: Specification.Sorted) {
        val column = table.columns.find { it.name == sortingSpec.columnId.key } ?: return
        if (sortingSpec.isAscending) {
            orderBy(column to SortOrder.ASC)
        } else {
            orderBy(column to SortOrder.DESC)
        }
    }

    /**
     * Try to get name for current value from foreign table for given column and it's value.
     *  @param column - column that can be the reference to another table
     *  @param value - if the [column] is reference, than it should be ID value
     *  @return value of column with name "name" or other text type if it's valid or null if not
     *  (no text type column was found, given [column] is not reference column or given [value] is not valid ID
     */
    private fun getNameFromForeignTable(column: Column<Any>, value: Any): Any? {
        // if column has foreign key:
        // then it's reference. get target table:
        val refTable = column.foreignKey?.targetTable as? IdTable<Comparable<Any>>
        // then value is reference id:
        val refID = value as? EntityID<Comparable<Any>>
        // find readable name as ref.table's column with name == "name" or any text column type:
        val refNameColumn =
            refTable?.columns?.find { it.name.lowercase() == "name" || it.columnType is TextColumnType } as? Column<Any>

        log("trying to get name from foreign table for $value: $refTable nameColumn: $refNameColumn uuid: $refID")
        val refName = if (refTable != null && refID != null && refNameColumn != null) {
            // if given column is reference - slice ref.table for given ID and get first result (it has to be one result here):
            refTable
                .select { refTable.id eq refID }
                .firstNotNullOfOrNull { result ->
                    // save id in reference table, readable name and column
                    result[refNameColumn]
                }
        } else {
            null
        }

        return refName
    }

}