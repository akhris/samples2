package ui.components.tables.mappers

import domain.Place
import domain.Worker
import persistence.exposed.dto.Tables
import ui.components.tables.Cell
import ui.components.tables.ColumnId
import ui.components.tables.IDataTableMapper

class WorkersDataMapper : IDataTableMapper<Worker> {
    override val columns: List<ColumnId>
        get() = listOf(
            Column.Name.id,
            Column.MiddleName.id,
            Column.Surname.id,
            Column.PhoneNumber.id,
            Column.Email.id,
            Column.Place.id
        )

    override fun getId(item: Worker): String {
        return item.id
    }

    override fun updateItem(item: Worker, columnId: ColumnId, cell: Cell): Worker {
        return when (Column.requireColumn(columnId)) {
            Column.Name -> (cell as? Cell.EditTextCell)?.let {
                item.copy(name = it.value)
            }

            Column.Email -> (cell as? Cell.EditTextCell)?.let {
                item.copy(email = it.value)
            }

            Column.MiddleName -> (cell as? Cell.EditTextCell)?.let {
                item.copy(middleName = it.value)
            }

            Column.PhoneNumber -> (cell as? Cell.EditTextCell)?.let {
                item.copy(phoneNumber = it.value)
            }

            Column.Surname -> (cell as? Cell.EditTextCell)?.let {
                item.copy(surname = it.value)
            }

            Column.Place -> (cell as? Cell.EntityCell)?.let {
                item.copy(place = (it.entity as? Place?))
            }
        } ?: item
    }

    override fun getCell(item: Worker, columnId: ColumnId): Cell {
        return when (Column.requireColumn(columnId)) {
            Column.Name -> Cell.EditTextCell(value = item.name)
            Column.MiddleName -> Cell.EditTextCell(value = item.middleName)
            Column.Surname -> Cell.EditTextCell(value = item.surname)
            Column.Email -> Cell.EditTextCell(value = item.email)
            Column.PhoneNumber -> Cell.EditTextCell(value = item.phoneNumber)
            Column.Place -> Cell.EntityCell(entity = item.place, entityClass = Place::class)
        }
    }

    private sealed class Column(val id: ColumnId) {
        object Name : Column(ColumnId(Tables.Workers.name.name, "??????"))
        object MiddleName : Column(ColumnId(Tables.Workers.middleName.name, "????????????????"))
        object Surname : Column(ColumnId(Tables.Workers.surname.name, "??????????????"))
        object Place : Column(ColumnId(Tables.Workers.room.name, "??????????????????"))
        object PhoneNumber : Column(ColumnId(Tables.Workers.phoneNumber.name, "??????????????"))
        object Email : Column(ColumnId(Tables.Workers.email.name, "e-mail"))

        companion object {
            fun requireColumn(id: ColumnId): Column {
                return when (id.key) {
                    Name.id.key -> Name
                    MiddleName.id.key -> MiddleName
                    Surname.id.key -> Surname
                    Place.id.key -> Place
                    PhoneNumber.id.key -> PhoneNumber
                    Email.id.key -> Email
                    else -> throw IllegalStateException("column with id: $id was not found in $this")
                }
            }
        }
    }
}