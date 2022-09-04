package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Place

class PlacesDao:IBaseDao<Place> {
    override suspend fun getByID(id: String): Place? {
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
    ): EntitiesList<Place> {
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

    override suspend fun update(entity: Place) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Place) {
        TODO("Not yet implemented")
    }
}