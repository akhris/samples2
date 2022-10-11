package domain.application.baseUseCases

import domain.EntitiesList
import domain.IEntity
import domain.IRepository
import domain.ISpecification
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

open class GetEntities<ENTITY : IEntity>(
    val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<EntitiesList<ENTITY>, GetEntities.Params>(ioDispatcher) {

    override suspend fun run(params: Params): EntitiesList<ENTITY> {
        return when (params) {
            is Params.GetWithSpecification -> getEntitiesWithSpec(params.specifications.toList())
        }
    }

    private suspend fun getEntitiesWithSpec(specifications: List<ISpecification>): EntitiesList<ENTITY> {
        return repo.query(specifications)
    }


    sealed class Params {

        class GetWithSpecification(vararg val specifications: ISpecification) : Params()
    }

}