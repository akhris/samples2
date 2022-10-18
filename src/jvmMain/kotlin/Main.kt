// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.window.WindowDraggableArea
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
import org.kodein.di.instance
import persistence.exposed.DbSettings
import test.SampleTypes
import ui.root_ui.RootUi
import ui.UiSettings
import ui.root_ui.IRootComponent
import ui.root_ui.RootComponent
import ui.theme.AppTheme


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
        var isDark by remember { mutableStateOf(false) }

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
                    onThemeChanged = { isDark = it },
                    windowPlacement = windowState.placement,
                    onWindowPlacementChange = { windowState.placement = it }
                )
            }

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