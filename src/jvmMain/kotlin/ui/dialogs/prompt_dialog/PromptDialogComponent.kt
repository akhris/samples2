package ui.dialogs.prompt_dialog

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.subscribe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import org.kodein.di.DI

class PromptDialogComponent(
    private val di: DI,
    componentContext: ComponentContext,
    message: String,
    title: String
) : IPromptDialog, ComponentContext by componentContext {
    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _state = MutableValue(IPromptDialog.State(title = title, message = message))

    override val state: Value<IPromptDialog.State> = _state

    init {
        componentContext
            .lifecycle
            .subscribe(onDestroy = {
                scope.coroutineContext.cancelChildren()
            })
    }
}