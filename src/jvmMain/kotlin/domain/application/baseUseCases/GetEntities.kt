package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.ISpecification
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetEntities<ENTITY : IEntity>(
    val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<List<ENTITY>, GetEntities.Params>(ioDispatcher) {

    override suspend fun run(params: Params): List<ENTITY> {
        return when (params) {
            is GetBySpecification -> repo.query(params.specification)
        }
    }

    sealed class Params
    data class GetBySpecification(val specification: ISpecification) : Params()
}