package domain.application

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Base UseCase class
 */
abstract class UseCase<out R, in P>(private val coroutineDispatcher: CoroutineDispatcher) where R : Any {
    /**
     * operator fun that runs the operation. If operation throws exception - returns [Result.Failure], otherwise
     * return [Result.Success]
     */
    suspend operator fun invoke(params: P): Result<R> {
        return try {
            // Moving all use case's executions to the injected dispatcher
            // In production code, this is usually the Default dispatcher (background thread)
            // In tests, this becomes a TestCoroutineDispatcher
            withContext(coroutineDispatcher) {
                run(params).let {
                    Result.Success(it)
                }
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    /**
     * Override this to set the code to be executed.
     */
    @Throws(RuntimeException::class)
    abstract suspend fun run(params: P): R


    class None
}