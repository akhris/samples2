package ui.screens.base_entity_screen

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.UpdateEntity
import kotlinx.coroutines.*
import org.kodein.di.DI
import org.kodein.di.instance
import utils.log

abstract class EntityComponent<T : IEntity>(
    di: DI,
    componentContext: ComponentContext
) : IEntityComponent<T>,
    ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val getEntities: GetEntities<T> by di.instance()
    private val insertEntity: InsertEntity<T> by di.instance()
    private val updatEntity: UpdateEntity<T> by di.instance()

    private val repositoryCallbacks: IRepositoryCallback<T> by di.instance()


    private val _state = MutableValue(IEntityComponent.State<T>())
    override val state: Value<IEntityComponent.State<T>> = _state

    override fun insertNewEntity(entity: T) {
        scope.launch {
            insertEntity(InsertEntity.Insert(entity))
        }
    }

    override fun updateEntity(entity: T) {
        scope.launch {
            updatEntity(UpdateEntity.Update(entity))
        }
    }


    private suspend fun invalidateEntities() {
        //get all samples
        val entities = getEntities(GetEntities.Params.GetWithSpecification(Specification.QueryAll))

        when (entities) {
            is Result.Success -> {
                _state.reduce {
                    it.copy(
                        entities = when (val list = entities.value) {
                            is EntitiesList.Grouped -> listOf()
                            is EntitiesList.NotGrouped -> list.items
                        }
                    )
                }
            }

            is Result.Failure -> {
                log(entities.throwable)
                log(entities.throwable.stackTraceToString())
            }
        }
    }

    init {
        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            invalidateEntities()
        }

        //subscribe to repository callbacks:
        scope.launch {
            repositoryCallbacks.updates.collect {
                when (it) {
                    is RepoResult.ItemInserted,
                    is RepoResult.ItemRemoved,
                    is RepoResult.ItemUpdated -> {
                        invalidateEntities()
                    }
                }
            }
        }

    }
}