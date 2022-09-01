package ui.screens.samples

import com.arkivanov.decompose.ComponentContext
import domain.Sample
import org.kodein.di.DI
import ui.screens.base_entity_screen.EntityComponent

class SamplesComponent(
    di: DI,
    componentContext: ComponentContext
) : EntityComponent<Sample>(di, componentContext)
//{
//
//    private val getSamples: GetEntities<Sample> by di.instance()
//    private val insertSample: InsertEntity<Sample> by di.instance()
//    private val updateSample: UpdateEntity<Sample> by di.instance()
//
//    private val repositoryCallbacks: IRepositoryCallback<Sample> by di.instance()
//
//    private val scope =
//        CoroutineScope(Dispatchers.Default + SupervisorJob())
//
//
//    private val _state = MutableValue(ISamples.State())
//    override val state: Value<ISamples.State> = _state
//
//    override fun insertNewSample(sample: Sample) {
//        scope.launch {
//            insertSample(InsertEntity.Insert(sample))
//        }
//    }
//
//    override fun updateSample(sample: Sample) {
//        scope.launch {
//            updateSample(UpdateEntity.Update(sample))
//        }
//    }
//
//    private suspend fun invalidateSamples() {
//        //get all samples
//        val samples = getSamples(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
//
//        when (samples) {
//            is Result.Success -> {
//                _state.reduce {
//                    it.copy(
//                        samples = when (val list = samples.value) {
//                            is EntitiesList.Grouped -> listOf()
//                            is EntitiesList.NotGrouped -> list.items
//                        }
//                    )
//                }
//            }
//
//            is Result.Failure -> {
//                log(samples.throwable)
//                log(samples.throwable.stackTraceToString())
//            }
//        }
//    }
//
//    init {
//        lifecycle.subscribe(onDestroy = {
//            scope.coroutineContext.cancelChildren()
//        })
//
//        scope.launch {
//            invalidateSamples()
//        }
//
//        //subscribe to repository callbacks:
//        scope.launch {
//            repositoryCallbacks.updates.collect {
//                when (it) {
//                    is RepoResult.ItemInserted,
//                    is RepoResult.ItemRemoved,
//                    is RepoResult.ItemUpdated -> {
//                        invalidateSamples()
//                    }
//                }
//            }
//        }
//
//    }
//}