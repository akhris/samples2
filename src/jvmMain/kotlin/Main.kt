// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import ui.screens.nav_host.INavHost
import ui.screens.nav_host.NavHostComponent
import ui.screens.nav_host.NavHostUi

@Composable
@Preview
fun App(rootComponent: INavHost) {
    MaterialTheme {
        NavHostUi(rootComponent)
    }
}

fun main() {
    // Create the root component before starting Compose
    val lifecycle = LifecycleRegistry()
    val root = NavHostComponent(componentContext = DefaultComponentContext(lifecycle))

    // Start Compose
    application {
        Window(onCloseRequest = ::exitApplication) {
            App(root)
        }
    }
}
