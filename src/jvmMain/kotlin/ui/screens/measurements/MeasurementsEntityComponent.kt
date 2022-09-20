package ui.screens.measurements

import com.arkivanov.decompose.ComponentContext
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import kotlinx.coroutines.launch
import org.kodein.di.DI
import org.kodein.di.instance
import ui.components.tables.mappers.MeasurementsDataMapper
import ui.screens.base_entity_screen.EntityComponent
import utils.log
import java.util.*

class MeasurementsEntityComponent(
    di: DI,
    componentContext: ComponentContext
) : EntityComponent<Measurement>(type = Measurement::class, di = di, componentContext = componentContext) {
    private val getParameters: GetEntities<Parameter> by di.instance()
    private val parametersCallbacks: IRepositoryCallback<Parameter> by di.instance()

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

}