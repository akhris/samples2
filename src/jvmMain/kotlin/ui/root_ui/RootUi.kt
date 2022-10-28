package ui.root_ui

import LocalSamplesType
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowScope
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.SampleType
import kotlinx.coroutines.flow.map
import org.kodein.di.compose.rememberDI
import org.kodein.di.instance
import settings.PreferencesManager
import ui.SideNavigationPanel
import ui.UiSettings
import ui.components.Tooltip
import ui.dialogs.edit_sample_type_dialog.EditSampleTypeDialogUi
import ui.screens.base_entity_screen.BaseEntityUi
import ui.screens.base_entity_screen.EntityUiwithFab
import ui.screens.preferences_screen.PreferencesUi
import ui.screens.sample_details_screen.SampleDetailsUi
import ui.utils.sampletypes_selector.SampleTypesSelectorUi
import kotlin.io.path.Path
import kotlin.io.path.nameWithoutExtension

@OptIn(ExperimentalDecomposeApi::class, ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun WindowScope.RootUi(
    component: IRootComponent,
    isDarkTheme: Boolean,
    onThemeChanged: (isDark: Boolean) -> Unit,
    windowPlacement: WindowPlacement,
    onMinimize: (() -> Unit)? = null,
    onWindowPlacementChange: (WindowPlacement) -> Unit,
    onAppClose: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed, confirmStateChange = { false })
    val scaffoldState = rememberScaffoldState(drawerState = drawerState)

    val prefsManager: PreferencesManager by rememberDI { instance() }
    val currentDBFileName by
    remember(prefsManager) { prefsManager.databaseFile.map { Path(it) } }.collectAsState(null)

    val navigationItem by remember(component) { component.currentDestination }.subscribeAsState()
//    val sampleTypes by remember(component) { component.sampleTypes }.subscribeAsState()
    var selectedSampleType by remember { mutableStateOf<SampleType?>(null) }

    var showAppClosePrompt by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            WindowDraggableArea(modifier = Modifier.combinedClickable(onDoubleClick = {
                when (windowPlacement) {
                    WindowPlacement.Maximized -> {
                        onWindowPlacementChange(WindowPlacement.Floating)
                    }

                    WindowPlacement.Floating -> {
                        onWindowPlacementChange(WindowPlacement.Maximized)
                    }

                    else -> {}
                }
            }) {

            }) {
                TopAppBar {
                    currentDBFileName?.let {
                        Tooltip(tip = it.toString(), title = "Текущий файл базы данных") {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = it.fileName.nameWithoutExtension,
                                style = MaterialTheme.typography.caption
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Children(stack = component.toolbarUtilsStack) {
                        when (val child = it.instance) {
                            is IRootComponent.ToolbarUtils.SampleTypesSelector -> SampleTypesSelectorUi(
                                component = child.component,
                                onSampleTypeSelected = {
                                    selectedSampleType = it
                                }, onEditSampleTypeClick = {
                                    component.showEditSampleTypeDialog(it)
                                })
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Tooltip(
                        tip = when (isDarkTheme) {
                            true -> "Включить светлую тему"
                            false -> "Включить тёмную тему"
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(horizontal = 8.dp).clickable {
                                    onThemeChanged(!isDarkTheme)
                                },
                            painter = when (isDarkTheme) {
                                true -> painterResource("vector/light_mode_black_24dp.svg")
                                false -> painterResource("vector/dark_mode_black_24dp.svg")
                            }, contentDescription = "light/dark theme switcher"
                        )
                    }
                    if (windowPlacement != WindowPlacement.Fullscreen && onMinimize != null) {
                        Tooltip(
                            tip = "Свернуть окно"
                        ) {
                            Icon(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp).clickable {
                                        onMinimize()
                                    },
                                painter = painterResource("vector/minimize_black_24dp.svg"),
                                contentDescription = "minimize window"
                            )
                        }
                    }
                    Tooltip(
                        tip = when (windowPlacement) {
                            WindowPlacement.Floating,
                            WindowPlacement.Maximized -> "Перейти в полноэкранный режим"

                            WindowPlacement.Fullscreen -> "Выйти из полноэкранного режима"
                        }
                    )
                    {
                        Icon(
                            modifier = Modifier
                                .padding(horizontal = 8.dp).clickable {
                                    onWindowPlacementChange(
                                        when (windowPlacement) {
                                            WindowPlacement.Floating,
                                            WindowPlacement.Maximized -> WindowPlacement.Fullscreen

                                            WindowPlacement.Fullscreen -> WindowPlacement.Floating
                                        }
                                    )
                                },
                            painter = when (windowPlacement) {
                                WindowPlacement.Floating,
                                WindowPlacement.Maximized -> painterResource("vector/fullscreen_black_24dp.svg")

                                WindowPlacement.Fullscreen -> painterResource("vector/fullscreen_exit_black_24dp.svg")
                            }, contentDescription = "fullscreen switcher"
                        )
                    }
                    Tooltip(
                        "Выйти из приложения"
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(horizontal = 8.dp).clickable {
                                    showAppClosePrompt = true
                                },
                            imageVector = Icons.Rounded.Close, contentDescription = "close the app"
                        )
                    }
                }
            }
        },
        content = {

            CompositionLocalProvider(LocalSamplesType provides selectedSampleType) {
                Row {
                    SideNavigationPanel(
                        isExpandable = false,
                        withLabels = true,
                        currentSelection = navigationItem,
                        onNavigationItemSelected = { component.navigateTo(it) })
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {


                        Children(stack = component.navHostStack, animation = stackAnimation(fade())) {
                            when (val child = it.instance) {
                                is IRootComponent.NavHost.Measurements -> ShowChildOrSamplesTypeHint {
                                    EntityUiwithFab(
                                        component = child.component
                                    )
                                }

                                is IRootComponent.NavHost.Norms -> ShowChildOrSamplesTypeHint { BaseEntityUi(component = child.component) }
                                is IRootComponent.NavHost.OperationTypes -> BaseEntityUi(component = child.component)
                                is IRootComponent.NavHost.Operations -> ShowChildOrSamplesTypeHint {
                                    EntityUiwithFab(
                                        component = child.component
                                    )
                                }

                                is IRootComponent.NavHost.Parameters -> ShowChildOrSamplesTypeHint {
                                    EntityUiwithFab(
                                        component = child.component
                                    )
                                }

                                is IRootComponent.NavHost.Places -> BaseEntityUi(component = child.component)
                                is IRootComponent.NavHost.SampleDetails -> SampleDetailsUi(component = child.component)
                                is IRootComponent.NavHost.Samples -> ShowChildOrSamplesTypeHint {
                                    EntityUiwithFab(
                                        component = child.component
                                    )
                                }

                                is IRootComponent.NavHost.Workers -> BaseEntityUi(component = child.component)
                                is IRootComponent.NavHost.AppPreferences -> PreferencesUi(component = child.component)
                            }
                        }


                    }
                }
            }
        }
    )

    Children(stack = component.dialogStack, animation = stackAnimation(slide())) {
        when (val child = it.instance) {
            IRootComponent.Dialog.None -> {}
            is IRootComponent.Dialog.AddSampleTypeDialog -> EditSampleTypeDialogUi(child.component, onDismiss = {
                component.dismissDialog()
            })
        }
    }

    if (showAppClosePrompt) {
        AlertDialog(
            modifier = Modifier.width(UiSettings.Dialogs.defaultAlertDialogWidth),
            onDismissRequest = {
                showAppClosePrompt = false
            }, text = { Text(text = "Выйти из приложения?") },
            confirmButton = {
                Button(onClick = {
                    onAppClose()
                }) {
                    Text("Выйти")
                }
            }, dismissButton = {
                TextButton(onClick = {
                    showAppClosePrompt = false
                }) {
                    Text("Отмена")
                }
            })
    }

}

@Composable
private fun BoxScope.ShowChildOrSamplesTypeHint(childContent: @Composable () -> Unit) {
    LocalSamplesType.current?.also {
        //sample type is not null -> show child content:
        childContent()
    } ?: run {
        //sample type is null -> show hint:
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Icon(
                        modifier = Modifier.size(64.dp),
                        painter = painterResource("vector/category_black_24dp.svg"),
                        contentDescription = "sample types"
                    )

                    Text("Выберите тип компонентов на верхней панели")

                }

            }
        }
    }
}