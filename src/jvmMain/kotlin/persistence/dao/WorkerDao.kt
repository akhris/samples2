package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Worker

class WorkerDao : IBaseDao<Worker> {
    override suspend fun getByID(id: String): Worker? {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun query(
        filterSpec: ISpecification?,
        sortingSpec: ISpecification?,
        pagingSpec: ISpecification?,
        searchSpec: ISpecification?,
        groupingSpec: ISpecification?
    ): EntitiesList<Worker> {
        TODO("Not yet implemented")
    }

    override suspend fun getItemsCount(
        filterSpec: ISpecification?,
        sortingSpec: ISpecification?,
        pagingSpec: ISpecification?,
        searchSpec: ISpecification?,
        groupingSpec: ISpecification?
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun update(entity: Worker) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Worker) {
        TODO("Not yet implemented")
    }
}