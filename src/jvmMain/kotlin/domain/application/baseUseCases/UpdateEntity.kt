package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Base use case to update [IEntity] in [IRepository].
 * May be overridden for more complex use cases.
 */
open class UpdateEntity<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<ENTITY, UpdateEntity.Params>(ioDispatcher) {

    override suspend fun run(params: Params): ENTITY {
        return when (params) {
            is Update -> (params.entityToUpdate as? ENTITY)?.let {
                update(it)
            } ?: throw IllegalArgumentException("Entity to update is not type of updater use case: $this")
        }
    }

    private suspend fun update(entity: ENTITY): ENTITY {
        repo.update(entity)
        return entity
    }

    sealed class Params
    data class Update(val entityToUpdate: Any) : Params()

}