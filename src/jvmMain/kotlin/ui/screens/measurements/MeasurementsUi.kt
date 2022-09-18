package ui.screens.measurements

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import domain.EntitiesList
import ui.components.tables.DataTable

@Composable
fun MeasurementsUi(component: IMeasurements) {

    val dataMapper by remember(component) { component.dataMapper }.subscribeAsState()

    val measurements by remember(component) { component.state }.subscribeAsState()

    when (val items = measurements.measurements) {
        is EntitiesList.Grouped -> {

        }

        is EntitiesList.NotGrouped -> {
            DataTable(
                modifier = Modifier.fillMaxWidth().horizontalScroll(state = rememberScrollState()),
                items = items.items,
                mapper = dataMapper
            )
        }
    }
}