package persistence.dao

import domain.EntitiesList
import domain.IBaseDao
import domain.ISpecification
import domain.Sample
import test.Samples
import utils.log

class SamplesDao : IBaseDao<Sample> {
    override suspend fun getByID(id: String): Sample? {
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
    ): EntitiesList<Sample> {
        log("querying samples in $this")
        return EntitiesList.NotGrouped(Samples.samples)
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

    override suspend fun update(entity: Sample) {
        TODO("Not yet implemented")
    }

    override suspend fun insert(entity: Sample) {
        TODO("Not yet implemented")
    }
}