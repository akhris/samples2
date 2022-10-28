package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher


/**
 * Base use case to remove [IEntity] from [IRepository].
 * May be overridden for more complex use cases.
 */
open class RemoveEntities<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<Unit, RemoveEntities.Params>(ioDispatcher) {

    override suspend fun run(params: Params) {
        return when (params) {
            is Remove -> (params.entitiesToRemove as? List<ENTITY>)?.let { remove(it) }
                ?: throw IllegalArgumentException("Entity to remove (${params.entitiesToRemove::class.simpleName}) is not type of remove use case: $this")
        }
    }

    private suspend fun remove(entities: List<ENTITY>) {
        repo.remove(entities)
    }

    sealed class Params
    data class Remove(val entitiesToRemove: List<Any>) : Params()

}