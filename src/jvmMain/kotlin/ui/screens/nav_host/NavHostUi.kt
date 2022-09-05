package ui.screens.nav_host

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import domain.IEntity
import ui.screens.base_entity_screen.BaseEntityUi
import ui.screens.norms.NormsUi
import ui.screens.operationtypes.OperationsTypesUi
import ui.screens.places.PlacesUi
import ui.screens.workers.WorkersUi
import utils.log
import kotlin.reflect.KClass


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun NavHostUi(component: INavHost) {
    Children(stack = component.childStack, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is INavHost.Child.Places -> PlacesUi(child.component)
            is INavHost.Child.Workers -> WorkersUi(child.component)
            is INavHost.Child.Norms -> NormsUi(child.component)
            is INavHost.Child.Parameters -> BaseEntityUi(child.component)
            is INavHost.Child.Operations -> BaseEntityUi(child.component, onPickNewEntity = { item, eClass ->
                log("going to pick new entity for $item of class: $eClass")
                //show dialog here:
                (eClass as? KClass<out IEntity>)?.let {
                    component.showEntityPicker(eClass)
                }
            })

            is INavHost.Child.Samples -> BaseEntityUi(child.component)
            is INavHost.Child.OperationTypes -> OperationsTypesUi(child.component)
        }
    }

    Children(stack = component.dialogStack, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is INavHost.Dialog.EntityPickerDialog<*> -> {
                log("going to show dialog to pick entity for ${child.component}")
                //show entity ui in a dialog:
                Dialog(onCloseRequest = {
                    component.dismissDialog()
                }) {
                    // TODO: Add callback for picked one here
                    // TODO: add single selection mode to DataTable
                    BaseEntityUi(component = child.component,
                        onPickNewEntity = { item, eClass ->
                            log("going to pick new entity for $item of class: $eClass")
                        })
                }
            }

            INavHost.Dialog.None -> {}
        }
    }
}