package ui

import domain.IEntity

sealed class OperationState<T> {
    class Empty<T> : OperationState<T>()

    //    class GoingToUpdate<T> : OperationState<T>()
    class UpdatedSuccessfully<T : IEntity>(val entity: T) : OperationState<T>()
    class UpdateFailure<T : IEntity>(val entity: T, val throwable: Throwable) : OperationState<T>()
}