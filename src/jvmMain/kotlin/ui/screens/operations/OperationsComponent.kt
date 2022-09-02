package ui.screens.operations

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

class OperationsComponent(
    private val di: DI,
    componentContext: ComponentContext
) : IOperations, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    private val getOperations: GetEntities<Operation> by di.instance()
    private val updateOperation: UpdateEntity<Operation> by di.instance()
    private val insertOperation: InsertEntity<Operation> by di.instance()

    private val repositoryCallbacks: IRepositoryCallback<Operation> by di.instance()


    private val _state: MutableValue<IOperations.State> = MutableValue(IOperations.State())
    override val state: Value<IOperations.State> = _state


    override fun insertOperation(operation: Operation) {
        scope.launch {
            insertOperation(InsertEntity.Insert(operation))
        }
    }

    override fun updateOperation(operation: Operation) {
        scope.launch {
            updateOperation(UpdateEntity.Update(operation))
        }
    }


    private suspend fun invalidateOperations() {
        val ops = getOperations(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        log("invalidateOperations: $ops")
        when (ops) {
            is Result.Success -> {
                _state.reduce {
                    it.copy(
                        operations = when (val list = ops.value) {
                            is EntitiesList.Grouped -> listOf()
                            is EntitiesList.NotGrouped -> list.items
                        }
                    )
                }
            }

            is Result.Failure -> {
                log(ops.throwable)
                log(ops.throwable.stackTraceToString())
            }
        }
    }

    init {
        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            invalidateOperations()
        }

        scope.launch {
            repositoryCallbacks
                .updates
                .collect {
                    when (it) {
                        is RepoResult.ItemInserted,
                        is RepoResult.ItemRemoved,
                        is RepoResult.ItemUpdated -> {
                            invalidateOperations()
                        }
                    }
                }
        }

    }

}