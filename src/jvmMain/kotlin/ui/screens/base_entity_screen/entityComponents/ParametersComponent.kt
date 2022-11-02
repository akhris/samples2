package ui.screens.base_entity_screen.entityComponents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.IconResource
import ui.components.tables.mappers.MeasurementsDataMapper
import ui.components.tables.mappers.ParametersDataMapper
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.FABParams
import utils.log

class ParametersComponent(
    di: DI,
    componentContext: ComponentContext
) : EntityComponentWithFab<Parameter>(Parameter::class, di, componentContext) {


    private val insertNorm: InsertEntity<Norm> by di.instance()


    override fun getFabParams(): List<FABParams> = listOf(
        FABParams(
            id = ACTION_ADD_PARAMETER,
            icon = IconResource.ImageVectorIcon(Icons.Rounded.Add),
            label = "Добавить параметр"
        ),
        FABParams(
            id = ACTION_ADD_NORM_CONDITION,
            icon = IconResource.PainterResourceIcon("vector/compare_arrows_black_24dp.svg"),
            label = "Добавить столбец норм"
        )
    )

    override fun invokeFABAction(id: String, tag: Any?) {
        when (id) {
            ACTION_ADD_PARAMETER -> {
                (tag as? SampleType)?.let { st ->
                    insertNewEntity(st)
                }
            }

            ACTION_ADD_NORM_CONDITION -> {
                showInputTextDialog(
                    title = "Добавить столбец норм",
                    caption = "условие применения нормы",
                    onYes = { condition ->
                        scope.launch {
                            invalidateDataMapper(listOf(condition))
                        }
                    })
            }
        }
    }

    override suspend fun doAfterEntitiesInvalidate(value: EntitiesList<Parameter>) {
        invalidateDataMapper()
    }

    private suspend fun invalidateDataMapper(additionalConditions: List<String> = listOf()) {
        val conditions = state.value.entities.flatten().flatMap { it.norms }.map { it.condition }.distinct()

        updateDataMapper {
            (it as? ParametersDataMapper)?.copy(conditions = conditions.plus(additionalConditions).distinct()) ?: it
        }
    }

    companion object {
        private const val ACTION_ADD_PARAMETER = "id_add_parameter"
        private const val ACTION_ADD_NORM_CONDITION = "id_add_norm_condition"
    }

}