package ui.dialogs.add_multiple_samples_dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import kotlinx.coroutines.delay
import ui.UiSettings
import ui.dialogs.BaseDialog
import utils.log

@Composable
fun AddMultipleSamplesUi(
    component: IAddMultipleSamplesComponent,
    onAdded: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val state by remember(component) { component.state }.subscribeAsState()
    var idsStringTemp by remember(state) { mutableStateOf(state.rawString) }


    BaseDialog(
        onDismiss = onDismiss,
        title = {
            Text(modifier = Modifier.padding(8.dp), text = "Добавить несколько образцов")
        },
        content = {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(value = idsStringTemp, onValueChange = { idsStringTemp = it }, label = {
                    Text("ID")
                }, trailingIcon = if (idsStringTemp.isNotEmpty()) {
                    {
                        Icon(modifier = Modifier.clickable {
                            idsStringTemp = ""
                        }, imageVector = Icons.Rounded.Clear, contentDescription = "clear ids text")
                    }
                } else null)

                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.caption) {
                    //show hint:
                    Text(
                        text =
                        """Введите ID или диапазоны ID, разделенные запятыми или пробелами.
Диапазоны могут содержать суффикс.
Например:
1, 5 10, 3-5, 10-20a
                        """
                    )

                    if (state.parsedIDs.isNotEmpty()) {
                        Text(text = "Будут добавлены образцы со следующими ID:")
                        val idsText = remember(state.parsedIDs) {
                            val resultString = StringBuilder()
                            state.parsedIDs.forEachIndexed { index, it ->
                                if (index != 0) {
                                    resultString.append(", ")
                                }
                                resultString.append(it)
                            }
                            resultString.toString()
                        }
                        Text(text = idsText)

                    }
                }

            }
        },
        buttons = {

        Button(enabled = state.parsedIDs.isNotEmpty(), onClick = {
                onAdded(state.parsedIDs)
                onDismiss()
            }) {

                val addText = remember(state.parsedIDs.size) {
                    if (state.parsedIDs.isEmpty()) {
                        ""
                    } else
                        " ${state.parsedIDs.size} образ${
                            when (state.parsedIDs.size % 10) {
                                1 -> "ец"
                                2, 3, 4 -> "цa"
                                else -> "цов"
                            }
                        }"
                }


                Text(
                    "Добавить${addText}"
                )
            }
        }
    )


    LaunchedEffect(idsStringTemp) {
        if (idsStringTemp == state.rawString) {
            return@LaunchedEffect
        }
        delay(UiSettings.Debounce.debounceTime)
        component.parseIDs(idsStringTemp)
    }


}