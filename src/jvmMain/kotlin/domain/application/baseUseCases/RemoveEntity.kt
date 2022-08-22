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
open class RemoveEntity<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<ENTITY, RemoveEntity.Params>(ioDispatcher) {

    override suspend fun run(params: Params): ENTITY {
        return when (params) {
            is Remove -> (params.entityToRemove as? ENTITY)?.let { remove(params.entityToRemove) }
                ?: throw IllegalArgumentException("Entity to remove is not type of remove use case: $this")
        }
    }

    private suspend fun remove(entity: ENTITY): ENTITY {
        repo.remove(entity)
        return entity
    }

    sealed class Params
    data class Remove(val entityToRemove: Any) : Params()

}