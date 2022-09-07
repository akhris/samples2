package ui.screens.base_entity_screen

import LocalSamplesType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import domain.IEntity
import domain.Specification
import kotlinx.coroutines.delay
import ui.UiSettings

/**
 * Wrapper of BaseEntityUi with Search/Add panel.
 */
@Composable
fun <T : IEntity> EntityUiWithSearchAddPanel(
    component: IEntityComponent<T>
) {


    val sampleType = LocalSamplesType.current

    Column(modifier = Modifier.fillMaxSize()) {

        SearhAddPanel(modifier = Modifier.align(Alignment.CenterHorizontally), onSearchStringChanged = {
            component.setQuerySpec(Specification.Search(it))
        })

        BaseEntityUi(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            component = component
        )

    }
}

@Composable
private fun SearhAddPanel(
    modifier: Modifier = Modifier,
    onSearchStringChanged: (String) -> Unit
) {
    var initialSearchString by remember { mutableStateOf("") }
    var searchString by remember(initialSearchString) { mutableStateOf(initialSearchString) }
    Row(
        modifier = modifier.fillMaxWidth(0.8f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        TextField(value = searchString, onValueChange = { searchString = it }, trailingIcon = {
            Icon(imageVector = Icons.Rounded.Clear, "очистить поле", modifier = Modifier.clickable {
                initialSearchString = ""
                onSearchStringChanged("")
            })
        })
    }

    //debounce:
    LaunchedEffect(searchString) {
        if (searchString == initialSearchString) {
            return@LaunchedEffect
        }
        delay(UiSettings.Debounce.debounceTime)
        initialSearchString = searchString
        onSearchStringChanged(searchString)
    }
}