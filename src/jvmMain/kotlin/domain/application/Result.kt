package domain.application

/**
 * A wrapper for handling failing requests
 */
sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<T>()
    data class Failure<T>(val throwable: Throwable) : Result<T>()

    override fun toString(): String {
        return when (this) {
            is Success -> "Success: value = $value"
            is Failure -> "Failure: why = $throwable"
        }
    }
}