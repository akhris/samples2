package ui.screens.base_entity_screen

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import domain.*
import ui.components.tables.IDataTableMapper
import ui.screens.base_entity_screen.filter_dialog.IFilterEntityFieldComponent
import ui.dialogs.error_dialog.IErrorDialogComponent
import ui.dialogs.file_picker_dialog.IFilePicker
import ui.dialogs.prompt_dialog.IPromptDialog
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.reflect.KClass

interface IEntityComponent<T : IEntity> {
    val state: Value<State<T>>

    val pagingSpec: Value<Specification.Paginated>

    val filterSpec: Value<Specification.Filtered>

    fun insertNewEntity(entity: T)
    fun insertNewEntity(sampleType: SampleType)
    fun updateEntity(entity: T)
    fun removeEntity(entity: Any)
    fun setSampleType(sampleType: SampleType)

    fun duplicateEntities(entities: List<T>)

    val dataMapper: Value<IDataTableMapper<T>>

    fun setQuerySpec(spec: Specification)
    fun resetQuerySpec(spec: Specification)

    fun addFilter(filterSpec: FilterSpec)

    fun removeFilter(filterSpec: FilterSpec)

    fun setPagingSpec(spec: Specification.Paginated)

    fun saveRowsToExcel(entities: List<T>)


    data class State<E : IEntity>(
        val entities: EntitiesList<E> = EntitiesList.empty()
    )

    val isReorderable: Boolean

    fun onEntitySelected(entity: T)

    /**
     * Exposes Router State
     */
    val dialogStack: Value<ChildStack<*, Dialog>>

    fun dismissDialog()

    fun showEntityPickerDialog(
        entity: IEntity?,
        entityClass: KClass<out IEntity>,
        onSelectionChanged: (IEntity?) -> Unit,
        columnName: String
    )

    fun showFilePickerDialog(
        title: String = "",
        fileFilters: List<FileNameExtensionFilter> = listOf(),
        onFileSelectedCallback: (filePath: String) -> Unit
    )

    fun showFilterDialog(columnFilters: FilterSpec)

    fun showPrompt(title: String, message: String, onYes: () -> Unit, onCancel: (() -> Unit)? = null)

    sealed class Dialog {
        object None : Dialog()
        class EntityPicker<T : IEntity>(
            val component: IEntityComponent<T>,
            val initialSelection: IEntity? = null,
            val onSelectionChanged: (IEntity?) -> Unit,
            val columnName: String = ""
        ) : Dialog()

        class FieldFilter<T : IEntity>(val component: IFilterEntityFieldComponent<T>) : Dialog()
        class ErrorDialog(val component: IErrorDialogComponent) : Dialog()
        class PromptDialog(val component: IPromptDialog, val onYes: () -> Unit, val onCancel: (() -> Unit)? = null) :
            Dialog()

        class FilePickerDialog(val component: IFilePicker) : Dialog()
    }

}