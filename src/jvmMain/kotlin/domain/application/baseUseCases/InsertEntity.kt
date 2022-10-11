package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher


/**
 * Base use case to insert [IEntity] to [IRepository].
 * May be overridden for more complex use cases.
 */
open class InsertEntity<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    private val entityCopier: ((ENTITY) -> ENTITY)? = null,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<ENTITY, InsertEntity.Params>(ioDispatcher) {

    override suspend fun run(params: Params): ENTITY {
        return when (params) {
            is Insert -> (params.entityToInsert as? ENTITY)?.let { insert(params.entityToInsert) }
                ?: throw IllegalArgumentException("Entity to insert is not type of insert use case: $this")
            is Copy -> (params.entityToCopy as? ENTITY)?.let { copy(params.entityToCopy) }
                ?: throw IllegalArgumentException("Entity to copy is not type of insert use case: $this")
        }
    }

    private suspend fun insert(entity: ENTITY): ENTITY {
        repo.insert(entity)
        return entity
    }

    private suspend fun copy(entity: ENTITY): ENTITY {
        val copiedEntity = entityCopier?.invoke(entity)
            ?: throw IllegalArgumentException("To copy an entity entityCopier must be provided: $this")
        repo.insert(copiedEntity)
        return copiedEntity
    }


    sealed class Params
    data class Insert(val entityToInsert: Any) : Params()
    data class Copy(val entityToCopy: Any) : Params()

}