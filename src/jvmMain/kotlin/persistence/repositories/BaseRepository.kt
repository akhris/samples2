package persistence.repositories

import domain.*
import kotlinx.coroutines.flow.SharedFlow

class BaseRepository<ENTITY : IEntity>(private val baseDao: IBaseDao<ENTITY>) : IRepository<ENTITY>,
    IRepositoryCallback<ENTITY> {
    private val repoCallbacks = RepositoryCallbacks<ENTITY>()
    override val updates: SharedFlow<RepoResult<ENTITY>> = repoCallbacks.updates

    override suspend fun getByID(id: String): ENTITY {
        return baseDao.getByID(id) ?: throw NotFoundInRepositoryException(
            what = "entity with id: $id",
            repository = this.toString()
        )
    }

    override suspend fun remove(specifications: List<ISpecification>) {
        TODO("Not yet implemented")
    }

    override suspend fun query(specifications: List<ISpecification>): EntitiesList<ENTITY> {
        val searchSpec = specifications.find { it is Specification.Search } as? Specification.Search
        return baseDao.query(searchSpec = searchSpec)
    }

    override suspend fun insert(t: ENTITY) {
        baseDao.insert(t)
        repoCallbacks.onItemInserted(t)
    }

    override suspend fun update(t: ENTITY) {
        baseDao.update(t)
        repoCallbacks.onItemUpdated(t)
    }

    override suspend fun remove(t: ENTITY) {
        baseDao.removeById(t.id)
        repoCallbacks.onItemUpdated(t)
    }


}