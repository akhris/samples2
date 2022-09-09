package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.ISpecification
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetItemsCount<ENTITY : IEntity>(
    private val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<Long, GetItemsCount.Params>(ioDispatcher) {

    override suspend fun run(params: Params): Long {
        return when (params) {
            is Params.GetBySpecifications -> {
                repo.getItemsCount(params.specs.toList())
            }
        }
    }

    sealed class Params {
        class GetBySpecifications(vararg val specs: ISpecification) : Params()
    }
}