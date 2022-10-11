package ui.screens.base_entity_screen.entityComponents

import com.arkivanov.decompose.ComponentContext
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.IconResource
import ui.components.tables.mappers.MeasurementsDataMapper
import ui.screens.base_entity_screen.EntityComponentWithFab
import ui.screens.base_entity_screen.FABParams
import utils.log

class MeasurementsEntityComponent(
    di: DI,
    componentContext: ComponentContext
) : EntityComponentWithFab<Measurement>(type = Measurement::class, di = di, componentContext = componentContext) {
    private val getParameters: GetEntities<Parameter> by di.instance()
    private val parametersCallbacks: IRepositoryCallback<Parameter> by di.instance()


    override fun getFabParams(): List<FABParams> = listOf(
        FABParams(
            ACTION_ADD_SINGLE,
            icon = IconResource.PainterResourceIcon("vector/plus_one_black_24dp.svg"),
            label = "Добавить запись"
        ),
        FABParams(
            ACTION_ADD_MULTIPLE,
            icon = IconResource.PainterResourceIcon("vector/playlist_add_black_24dp.svg"),
            label = "Добавить несколько"
        ),
        FABParams(
            ACTION_IMPORT_FROM_FILE,
            icon = IconResource.PainterResourceIcon("vector/file_download_black_24dp.svg"),
            label = "Импортировать из файла"
        )
    )

    override fun invokeFABAction(id: String, tag: Any?) {
        when (id) {
            ACTION_ADD_SINGLE -> {
                (tag as? SampleType)?.let { st ->
                    insertNewEntity(st)
                }
            }

            ACTION_ADD_MULTIPLE -> {

            }

            ACTION_IMPORT_FROM_FILE -> {

            }
        }
    }

    private suspend fun invalidateDataMapper() {
        val parameters = getParameters(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        if (parameters is Result.Success) {
            (parameters.value as? EntitiesList.NotGrouped<Parameter>)?.let { params ->
                updateDataMapper {
                    (it as? MeasurementsDataMapper)?.let { mdm ->
//                        mdm.parameters = params.items
                        log("invalidating data mapper: $mdm with params: ${params.items}")
                        mdm.copy(parameters = params.items)
                    } ?: it
                }
            }
        }
    }


//    override fun duplicateEntities(entities: List<Measurement>) {
//        scope.launch {
//            val duplicated =
//                entities
//                    .map { msrmnt ->
//                        msrmnt.copy(
//                            id = UUID.randomUUID().toString(),
//                            results = msrmnt
//                                .results
//                                .map {
//                                    it.copy(
//
//                                    )
//                                }
//                        )
//                    }
//        }
//    }


    init {

        scope.launch {
            invalidateDataMapper()
        }

        scope.launch {
            parametersCallbacks
                .updates
                .collect {
                    invalidateDataMapper()
                }
        }
    }


    companion object {
        private const val ACTION_ADD_SINGLE = "id_add_single"
        private const val ACTION_ADD_MULTIPLE = "id_add_multiple"
        private const val ACTION_IMPORT_FROM_FILE = "id_import_from_file"
    }

}