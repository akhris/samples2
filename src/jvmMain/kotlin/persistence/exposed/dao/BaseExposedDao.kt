package persistence.exposed.dao

import domain.*
import domain.valueobjects.SliceResult
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
    protected abstract fun insertStatement(entity: ENTITY): TABLE.(InsertStatement<*>) -> Unit

    protected open fun Transaction.doAfterUpdate(entity: ENTITY) {}
    protected open fun Transaction.doAfterInsert(entity: ENTITY) {}

    override suspend fun query(specs: List<ISpecification>): EntitiesList<ENTITY> {
        return newSuspendedTransaction {

            val withGrouping = specs.filterIsInstance<Specification.Grouped>().isNotEmpty()

            if (withGrouping) {
                getGroupedItems(specs)
            } else {
                val query = getNotGroupedQuery(specs)
                listOf(GroupedItem(
                    items = entityClass
                        .wrapRows(query)
                        .map { mapToEntity(it) }
                ))


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

    override suspend fun update(entities: List<ENTITY>) {
        log("going to make batch update for $entities")
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            entities.forEach { entity ->
                table.update(where = { table.id eq entity.id.toUUID() }, body = updateStatement(entity))
                doAfterUpdate(entity)
            }
            if (withCommit)
                commit()
//
//            BatchUpdateStatement(table).apply {
//                entities.forEach { entity: ENTITY ->
//                    addBatch(EntityID(entity.id.toUUID(), this@BaseExposedDao.table))
//                    updateStatement(entity)
//                    doAfterUpdate(entity)
//                }
            //fixme: empty batch data
//                log("batch data:")
//                this.data.forEach {
//                    log(it)
//                }
//                execute(this@newSuspendedTransaction)
//            }
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


    override suspend fun insert(entities: List<ENTITY>) {
//        val uuidTable = (table as? UUIDTable)?.also {
//            log("cannot use batch insert with no-uuid table: $it")
//        } ?: return
//
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.batchInsert(data = entities, shouldReturnGeneratedValues = false) { entity ->
                this[this@BaseExposedDao.table.id] = entity.id.toUUID()
                insertStatement(entity).invoke(this@BaseExposedDao.table, this@batchInsert)
            }
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

    override suspend fun removeByIDs(ids: List<String>) {
        newSuspendedTransaction {
            addLogger(StdOutSqlLogger)
            table.deleteWhere { table.id inList ids.map { it.toUUID() } }
            if (withCommit)
                commit()
        }
    }

    private fun getNotGroupedQuery(specs: List<ISpecification>): Query {
        val query = table.selectAll()
        (specs.filterIsInstance<Specification.Search>().firstOrNull())?.let {
            query.addSearching(it)
        }
        (specs.filterIsInstance<Specification.Filtered>()).forEach {
            //add filtering
            query.addFiltering(it)
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


    private fun getGroupedItems(specs: List<ISpecification>): EntitiesList<ENTITY> {
        // get entity column ID to group by from spec:
        val groupedBy = (specs.filterIsInstance<Specification.Grouped>().firstOrNull())?.groupingSpec?.columnId
            ?: return listOf()

        val column =
            (table.columns.find { it.name == groupedBy.key } as? Column<Any?>) ?: return listOf()

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
        return groupedItems
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

    private fun Query.addFiltering(filterSpec: Specification.Filtered) {
        filterSpec.filters.forEach { fSpec ->
            val column = table.columns.find { it.name == fSpec.columnName } as? Column<Any> ?: return@forEach
            when (fSpec) {
                is FilterSpec.Range<*> -> {}
                is FilterSpec.Values -> {
                    fSpec
                        .filteredValues
                        .forEach {
                            when (filterSpec.isFilteredOut) {
                                true -> andWhere { column neq it }
                                false -> orWhere { column eq it }
                            }
                        }
                }
            }
        }
    }


    /**
     * Try to get name for current value from foreign table for given column and it's value.
     *  @param column - column that can be the reference to another table
     *  @param value - if the [column] is reference, than it should be ID value
     *  @return value of column with name "name" or other text type if it's valid or null if not
     *  (no text type column was found, given [column] is not reference column or given [value] is not valid ID
     */
    private fun getNameFromForeignTable(column: Column<Any?>, value: Any?): Any? {
        // if column has foreign key:
        // then it's reference. get target table:
        val refTable = column.foreignKey?.targetTable as? IdTable<Comparable<Any>> ?: return null
        // then value is reference id:
        val refID = value as? EntityID<Comparable<Any>> ?: return null
        // find readable name as ref.table's column with name == "name" or any text column type:
        val refNameColumn =
            refTable.columns.find { it.name.lowercase() == "name" } as? Column<Any>

        log("trying to get name from foreign table for $value: $refTable nameColumn: $refNameColumn uuid: $refID")
        val refName = refTable
            .select { refTable.id eq refID }
            .firstNotNullOfOrNull { result ->
                // save id in reference table, readable name and column
                // if name column was found - get it
                (refNameColumn?.let { result[it].toString() } ?: "").ifEmpty {
                    val stringBuilder = StringBuilder()
                    refTable
                        .columns
                        .forEachIndexed { index, it ->

                            val text = result[it].toString()
                            if (text.isNotEmpty() && text != value.toString()) {
                                stringBuilder.append(text)
                                if (index < refTable.columns.size - 1) {
                                    stringBuilder.append(" ")
                                }
                            }
                        }
                    stringBuilder.toString()
                }
            }
        return refName
    }

    private fun getAllValuesFromForeignTable(column: Column<Any?>, id: Any?): List<Any> {
        // if column has foreign key:
        // then it's reference. get target table:
        val refTable = column.foreignKey?.targetTable as? IdTable<Comparable<Any>> ?: return listOf()
        // then value is reference id:
        val refID = id as? EntityID<Comparable<Any>> ?: return listOf()

        log("trying to get all values from foreign table for $refID: $refTable")
        val refValues = refTable
            .select { refTable.id eq refID }
            .firstNotNullOfOrNull { result ->
                // save id in reference table, readable name and column
                refTable
                    .columns
                    .mapNotNull { c: Column<*> ->
                        //if c is also a foreign reference - try to get name from it
                        (c as? Column<Any?>)?.let {
                            getNameFromForeignTable(c, result[c])
                        } ?: result[c]
                    }
            } ?: listOf()
        return refValues
    }

    override suspend fun slice(columnName: String): List<SliceResult> {
        return newSuspendedTransaction {
            val column =
                table.columns.find { it.name == columnName } as? Column<Any?> ?: return@newSuspendedTransaction listOf()

            val refTable = column.foreignKey?.targetTable

            val sliceItems =
                table
                    .slice(column)
                    .selectAll()
                    .withDistinct(true)
                    .mapNotNull { rr ->
                        val a = rr.getOrNull(column) ?: return@mapNotNull null
                        if (refTable != null) {
                            SliceResult(
                                a,
                                getAllValuesFromForeignTable(column, a)
                            )
                        } else SliceResult(a)
                    }


            sliceItems
        }
    }

}