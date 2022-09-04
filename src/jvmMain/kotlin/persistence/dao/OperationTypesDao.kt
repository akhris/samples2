package persistence.dao

import domain.*

class OperationTypesDao: IBaseDao<OperationType> {
    override suspend fun getByID(id: String): OperationType? {
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
    ): EntitiesList<OperationType> {
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

    override suspend fun update(entity: OperationType) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: OperationType) {
        TODO("Not yet implemented")
    }
}