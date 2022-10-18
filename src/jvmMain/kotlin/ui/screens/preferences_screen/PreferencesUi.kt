package ui.screens.preferences_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ui.dialogs.file_picker_dialog.IFilePicker
import ui.dialogs.file_picker_dialog.fileChooserDialog

@Composable
fun PreferencesUi(component: IPreferencesComponent) {
    val prefs by remember(component) { component.prefs }.subscribeAsState()

    PreferencesContent(prefs) { changedPref ->
        component.updatePref(changedPref)
    }

}

@Composable
private fun PreferencesContent(prefs: List<PreferenceItem>, onPrefChanged: (PreferenceItem) -> Unit) {

    Column {
        prefs.forEach {
            RenderPreferenceItem(it, onPrefChanged)
        }
    }

}

@Composable
private fun ColumnScope.RenderPreferenceItem(pref: PreferenceItem, onPrefChanged: (PreferenceItem) -> Unit) {
    when (pref) {
        is PreferenceItem.FilePreference -> RenderFilePreference(pref, onPrefChanged)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ColumnScope.RenderFilePreference(
    filePref: PreferenceItem.FilePreference,
    onPrefChanged: (PreferenceItem) -> Unit
) {
    ListItem(text = {
        Text(text = filePref.path)
    }, trailing = {
        Icon(modifier = Modifier.clickable {
            //open filepicker
            val newFile = fileChooserDialog(
                title = "Открыть файл базы данных",
                pickerType = IFilePicker.PickerType.SaveFile
            )

            if (newFile != null && filePref.path != newFile.path) {
                onPrefChanged(filePref.copy(path = newFile.path))
            }
        }, painter = painterResource("vector/folder_black_24dp.svg"), contentDescription = "open folder")
    })
}