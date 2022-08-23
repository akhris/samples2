package domain.application.baseUseCases

import domain.EntitiesList
import domain.IEntity
import domain.IRepository
import domain.ISpecification
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher
import utils.log

open class GetEntities<ENTITY : IEntity>(
    val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<EntitiesList<ENTITY>, GetEntities.Params>(ioDispatcher) {

    override suspend fun run(params: Params): EntitiesList<ENTITY> {
        return when (params) {
            is Params.GetWithSpecification -> getEntitiesWithSpec(params.specification)
        }
    }

    private suspend fun getEntitiesWithSpec(specification: ISpecification): EntitiesList<ENTITY> {
        return repo.query(specification)
    }


    sealed class Params {

        class GetWithSpecification(val specification: ISpecification) : Params()
    }

}