// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import di.di
import domain.SampleType
import domain.application.baseUseCases.InsertEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.kodein.di.instance
import persistence.exposed.DbSettings
import settings.PreferencesManager
import test.SampleTypes
import ui.root_ui.RootUi
import ui.UiSettings
import ui.dialogs.file_picker_dialog.IFilePicker
import ui.dialogs.file_picker_dialog.fileChooserDialog
import ui.root_ui.RootComponent
import ui.theme.AppTheme
import utils.log
import javax.swing.JOptionPane
import javax.swing.JOptionPane.OK_CANCEL_OPTION
import javax.swing.JOptionPane.YES_NO_OPTION


fun main() {

    //load settings from swaydb

    val prefs by di.instance<PreferencesManager>()
    // check settings from swaydb and initiate Database


    val db = connectToDatabase(preferencesManager = prefs) ?: return


    // Create the root component before starting Compose
    val lifecycle = LifecycleRegistry()
    val root = RootComponent(componentContext = DefaultComponentContext(lifecycle), di = di)

    // Start Compose
    application {

        var isAppOpen by remember { mutableStateOf(true) }


        val windowState = rememberWindowState(
            width = UiSettings.Window.initialWidth,
            height = UiSettings.Window.initialHeight,
            position = WindowPosition(
                Alignment.Center
            )
        )
        var isDark by remember(prefs) { mutableStateOf(prefs.isDarkMode()) }



        if (!isAppOpen)
            return@application



        Window(
            state = windowState,
            title = "Samples",
            icon = painterResource("vector/memory_black_24dp.svg"),
            onCloseRequest = ::exitApplication,
            undecorated = true
        ) {

            AppTheme(darkTheme = isDark) {
                RootUi(
                    component = root,
                    isDarkTheme = isDark,
                    onThemeChanged = {
                        isDark = it
                        prefs.setIsDarkMode(it)
                    },
                    windowPlacement = windowState.placement,
                    onWindowPlacementChange = { windowState.placement = it },
                    onAppClose = {
                        isAppOpen = false
                    }
                )
            }

        }
    }
}


private fun connectToDatabase(dbFile: String? = null, preferencesManager: PreferencesManager): Database? {
    val filePath = dbFile ?: preferencesManager.getDatabaseFile()
    val db = DbSettings.connectToDB(filePath)
    if (db != null) {
        if (filePath != preferencesManager.getDatabaseFile()) {
            preferencesManager.setDatabaseFile(filePath)
        }
        return db
    }
    //db==null, connection failed
    //yes = 0, no = 1
    val answer = JOptionPane.showConfirmDialog(
        null,
        "$filePath\n\nChoose another file?",
        "Cannot connect to database.",
        YES_NO_OPTION
    )

    if (answer == 0) {
        val file = fileChooserDialog(title = "Выберите файл базы данных", pickerType = IFilePicker.PickerType.SaveFile)
            ?: return null
        return connectToDatabase(file.path, preferencesManager)
    }

    return null
}

private fun prepopulateDB() {
    val insertSampleType by di.instance<InsertEntity<SampleType>>()
    CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
        SampleTypes.list.forEach {
            insertSampleType(InsertEntity.Insert(it))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ShowCheckDatabaseAlert(
    dbFile: String,
    onDismiss: () -> Unit,
    onSelectAnotherDBFile: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ошибка при открытии файла базы данных") },
        text = { Text(dbFile) },
        dismissButton = {
            TextButton(onClick = { onSelectAnotherDBFile(PreferencesManager.getDefaultDatabaseFile()) }) {
                Text("Файл по умолчанию")
            }
        },
        confirmButton = {
            Button(onClick = {
                val f =
                    fileChooserDialog(title = "Выберите файл базы данных", pickerType = IFilePicker.PickerType.SaveFile)
                if (f != null) {
                    onSelectAnotherDBFile(f.path)
                } else {
                    onDismiss()
                }
            }) {
                Text("Выбрать другой")
            }

        }
    )
}