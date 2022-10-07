package domain.application.baseUseCases

import domain.IEntity
import domain.IRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetSlice<ENTITY : IEntity>(
    val repo: IRepository<ENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<List<Any>, GetSlice.Params>(ioDispatcher) {

    override suspend fun run(params: Params): List<Any> {
        return when (params) {
            is Params.GetSliceForColumn -> {
                repo.getSlice(params.columnName)
            }
        }
    }


    sealed class Params {
        class GetSliceForColumn(val columnName: String) : Params()
    }
}