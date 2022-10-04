package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

open class UpdateEntities<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<List<ENTITY>, UpdateEntities.Params>(ioDispatcher) {

    override suspend fun run(params: Params): List<ENTITY> {
        return when (params) {
            is Update -> (params.entities as? List<ENTITY>)?.let {
                update(it)
            } ?: throw IllegalArgumentException("Entity to update is not type of updater use case: $this")
        }
    }

    private suspend fun update(entities: List<ENTITY>): List<ENTITY> {
        repo.update(entities)
        return entities
    }


    sealed class Params

    data class Update(val entities: List<Any>) : Params()
}