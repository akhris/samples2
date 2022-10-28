package persistence.repositories

import domain.*
import domain.valueobjects.SliceResult
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

//    override suspend fun remove(specifications: List<ISpecification>) {
//        TODO("Not yet implemented")
//    }

    override suspend fun query(specifications: List<ISpecification>): EntitiesList<ENTITY> {
        return baseDao.query(specs = specifications)
    }

    override suspend fun getItemsCount(specifications: List<ISpecification>): Long {
        return baseDao.getItemsCount(specs = specifications)
    }

    override suspend fun insert(t: ENTITY) {
        baseDao.insert(t)
        repoCallbacks.onItemInserted(t)
    }

    override suspend fun insert(t: List<ENTITY>) {
        baseDao.insert(t)
        t.lastOrNull()?.let {
            repoCallbacks.onItemInserted(it)
        }
    }

    override suspend fun update(t: ENTITY) {
        baseDao.update(t)
        repoCallbacks.onItemUpdated(t)
    }


    override suspend fun update(t: List<ENTITY>) {
        baseDao.update(t)
        t.firstOrNull()?.let {
            repoCallbacks.onItemUpdated(it)
        }
    }


    override suspend fun remove(t: ENTITY) {
        baseDao.removeById(t.id)
        repoCallbacks.onItemUpdated(t)
    }

    override suspend fun remove(t: List<ENTITY>) {
        baseDao.removeByIDs(t.map { it.id })
        t.firstOrNull()?.let {
            repoCallbacks.onItemRemoved(it)
        }
    }

    override suspend fun getSlice(columnName: String): List<SliceResult> {
        return baseDao.slice(columnName)
    }


}