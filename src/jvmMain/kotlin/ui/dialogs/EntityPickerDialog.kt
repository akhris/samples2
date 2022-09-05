package ui.dialogs

import LocalSamplesType
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import di.di
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import org.kodein.di.LazyDelegate
import org.kodein.di.instance
import ui.UiSettings
import ui.components.tables.DataTable
import ui.components.tables.IDataTableMapper
import ui.components.tables.SelectionMode
import utils.log
import kotlin.reflect.KClass

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : IEntity> EntityPickerDialog(
    onDismiss: () -> Unit,
    entityClass: KClass<out T>,
    initialSelection: String? = null,
    onSelectionChanged: ((T?) -> Unit)? = null
) {

    val getEntities: GetEntities<T> by when (entityClass) {
        Sample::class -> di.instance<GetEntities<Sample>>()
        SampleType::class -> di.instance<GetEntities<SampleType>>()
        Parameter::class -> di.instance<GetEntities<Parameter>>()
        Operation::class -> di.instance<GetEntities<Operation>>()
        OperationType::class -> di.instance<GetEntities<OperationType>>()
        Worker::class -> di.instance<GetEntities<Worker>>()
        Place::class -> di.instance<GetEntities<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $entityClass")
    } as LazyDelegate<GetEntities<T>>


    val insertEntity: InsertEntity<T> by when (entityClass) {
        Sample::class -> di.instance<InsertEntity<Sample>>()
        SampleType::class -> di.instance<InsertEntity<SampleType>>()
        Parameter::class -> di.instance<InsertEntity<Parameter>>()
        Operation::class -> di.instance<InsertEntity<Operation>>()
        OperationType::class -> di.instance<InsertEntity<OperationType>>()
        Worker::class -> di.instance<InsertEntity<Worker>>()
        Place::class -> di.instance<InsertEntity<Place>>()
        else -> throw IllegalArgumentException("unsupported type: $entityClass")
    } as LazyDelegate<InsertEntity<T>>

    val dataMapper: IDataTableMapper<T> by when (entityClass) {
        Sample::class -> di.di.instance<IDataTableMapper<Sample>>()
        SampleType::class -> di.di.instance<IDataTableMapper<SampleType>>()
        Parameter::class -> di.di.instance<IDataTableMapper<Parameter>>()
        Operation::class -> di.di.instance<IDataTableMapper<Operation>>()
        OperationType::class -> di.di.instance<IDataTableMapper<OperationType>>()
        Worker::class -> di.di.instance<IDataTableMapper<Worker>>()
        Place::class -> di.di.instance<IDataTableMapper<Place>>()
        else -> throw IllegalArgumentException("cannot get data table mapper!")
    } as LazyDelegate<IDataTableMapper<T>>


    var entities by remember { mutableStateOf(listOf<T>()) }

    var selectedEntities by remember { mutableStateOf<List<T>>(listOf()) }

    var insertNewEntity by remember { mutableStateOf(false) }

    val currentType = LocalSamplesType.current


    AlertDialog(
        onDismissRequest = onDismiss,

        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
                OutlinedButton(onClick = {
                    // TODO: add entity row here
                    insertNewEntity = true

                }) {
                    Text("Добавить")
                }
                Button(onClick = {
                    onSelectionChanged?.invoke(selectedEntities.firstOrNull())
                    onDismiss()
                }) {
                    Text("Выбрать")
                }
            }
        },
        text = {
            DataTable(
                modifier = Modifier.size(
                    width = UiSettings.Dialogs.defaultAlertDialogWidth,
                    height = UiSettings.Dialogs.defaultWideDialogHeight
                ),
                items = entities,
                mapper = dataMapper,
                selectionMode = SelectionMode.Single(initialSelection),
                onSelectionChanged = { selection ->
                    selectedEntities = entities.filter { e -> e.id in selection }
                }
            )
        }
    )


    LaunchedEffect(getEntities) {
        val list = getEntities(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        when (list) {
            is Result.Failure -> {
                log("cannot get entities for $entityClass")
                log(list.throwable.localizedMessage)
            }

            is Result.Success -> {
                when (list.value) {
                    is EntitiesList.Grouped -> {

                    }

                    is EntitiesList.NotGrouped -> {
                        entities = list.value.items
                    }
                }
            }
        }
    }

    LaunchedEffect(insertNewEntity) {
        if (insertNewEntity) {
            val newEntity = when (entityClass) {
                Sample::class -> Sample(type = currentType ?: throw IllegalArgumentException("current type is null"))
                SampleType::class -> SampleType()
                Parameter::class -> Parameter(
                    sampleType = currentType ?: throw IllegalArgumentException("current type is null")
                )

                Operation::class -> Operation()
                OperationType::class -> OperationType()
                Worker::class -> Worker()
                Place::class -> Place()
                else -> throw IllegalArgumentException("unsupported type: $entityClass")
            }
            insertEntity(InsertEntity.Insert(newEntity))

            insertNewEntity = false
        }
    }


}