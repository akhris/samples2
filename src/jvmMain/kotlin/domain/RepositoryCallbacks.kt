package domain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Base implementation of [IRepositoryCallback].
 */
class RepositoryCallbacks<T> : IRepositoryCallback<T> {
    private val _updates = MutableSharedFlow<RepoResult<T>>(replay = 0)
    override val updates: SharedFlow<RepoResult<T>> = _updates

    suspend fun onItemUpdated(item: T) {
        _updates.emit(RepoResult.ItemUpdated(item))
    }

    suspend fun onItemRemoved(item: T) {
        _updates.emit(RepoResult.ItemRemoved(item))
    }

    suspend fun onItemInserted(item: T) {
        _updates.emit(RepoResult.ItemInserted(item))
    }
}