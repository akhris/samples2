package ui.dialogs.error_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import org.kodein.di.DI

class ErrorDialogComponent(
    private val title: String = "",
    private val caption: String = "",
    private val error: Throwable? = null,
    private val di: DI,
    componentContext: ComponentContext
) : IErrorDialogComponent, ComponentContext by componentContext {

    private val _state = MutableValue(IErrorDialogComponent.State(title = title, caption = caption, error = error))

    override val state: Value<IErrorDialogComponent.State> = _state

}