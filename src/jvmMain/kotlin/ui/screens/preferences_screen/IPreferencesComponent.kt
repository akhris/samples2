package ui.screens.preferences_screen

import com.arkivanov.decompose.value.Value

interface IPreferencesComponent {
    val prefs: Value<List<PreferenceItem>>


    fun updatePref(pref: PreferenceItem)
}