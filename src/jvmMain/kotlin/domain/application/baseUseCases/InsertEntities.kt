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
open class InsertEntities<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    private val entityCopier: ((ENTITY) -> ENTITY)? = null,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<Unit, InsertEntities.Params>(ioDispatcher) {

    override suspend fun run(params: Params) {
        when (params) {
            is Insert -> (params.entitiesToInsert as? List<ENTITY>)?.let { insert(params.entitiesToInsert) }
                ?: throw IllegalArgumentException("Entity to insert is not type of insert use case: $this")

        }
    }

    private suspend fun insert(entities: List<ENTITY>) {
        repo.insert(entities)
    }

    private suspend fun copy(entity: ENTITY): ENTITY {
        val copiedEntity = entityCopier?.invoke(entity)
            ?: throw IllegalArgumentException("To copy an entity entityCopier must be provided: $this")
        repo.insert(copiedEntity)
        return copiedEntity
    }


    sealed class Params
    data class Insert(val entitiesToInsert: List<Any>) : Params()
//    data class Copy(val entityToCopy: Any) : Params()

}