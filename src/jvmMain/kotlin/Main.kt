// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import di.di
import domain.SampleType
import domain.application.baseUseCases.InsertEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.kodein.di.instance
import persistence.exposed.DbSettings
import test.SampleTypes
import ui.root_ui.RootUi
import ui.UiSettings
import ui.root_ui.IRootComponent
import ui.root_ui.RootComponent
import ui.theme.AppTheme


@Composable
@Preview
fun App(rootComponent: IRootComponent) {

    var isDark by remember { mutableStateOf(false) }
    AppTheme(darkTheme = isDark) {
//        VerticalReorderList()
        RootUi(component = rootComponent, isDarkTheme = isDark, onThemeChanged = { isDark = it })
    }
}

fun main() {
    // check settings from swaydb and initiate Database
    DbSettings.db
    //    prepopulateDB()
    // Create the root component before starting Compose
    val lifecycle = LifecycleRegistry()
    val root = RootComponent(componentContext = DefaultComponentContext(lifecycle), di = di)
    // Start Compose
    application {
        val windowState = rememberWindowState(
            width = UiSettings.Window.initialWidth,
            height = UiSettings.Window.initialHeight,
            position = WindowPosition(
                Alignment.Center
            )
        )
        Window(
            state = windowState,
            title = "Samples",
            onCloseRequest = ::exitApplication,
            undecorated = false
        ) {
            App(root)
        }
    }
}


private fun prepopulateDB() {
    val insertSampleType by di.instance<InsertEntity<SampleType>>()
    CoroutineScope(Dispatchers.Default + SupervisorJob()).launch {
        SampleTypes.list.forEach {
            insertSampleType(InsertEntity.Insert(it))
        }
    }
}