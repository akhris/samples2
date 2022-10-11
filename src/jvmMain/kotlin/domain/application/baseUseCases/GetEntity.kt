package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Base use case to get [IEntity] from [IRepository] by ID.
 * May be overridden for more complex use cases.
 */
open class GetEntity<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<ENTITY, GetEntity.Params>(ioDispatcher) {

    override suspend fun run(params: Params): ENTITY {
        return when (params) {
            is GetByID -> getByID(params.id)
        }
    }

    private suspend fun getByID(id: String): ENTITY {
        return repo.getByID(id)
    }

    sealed class Params
    data class GetByID(val id: String) : Params()


}