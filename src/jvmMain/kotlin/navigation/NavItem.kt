package navigation

import domain.Sample

/**
 * Class representing navigation items for using in NavHost navigation.
 * Each Item has title, icon and route String.
 */
sealed class NavItem(val pathToIcon: String, val title: String) {

    object Workers : NavItem(
        pathToIcon = "vector/people_black_24dp.svg",
        title = "Сотрудники"
    )

    object Samples : NavItem(
        pathToIcon = "vector/storage_black_24dp.svg",
        title = "Образцы"
    )

    object Places : NavItem(
        pathToIcon = "vector/room_black_24dp.svg",
        title = "Места"
    )

    object Operations : NavItem(
        pathToIcon = "vector/engineering_black_24dp.svg",
        title = "Операции"
    )

    object OperationTypes : NavItem(
        pathToIcon = "vector/engineering_black_24dp.svg",
        title = "Типы операций"
    )

    object Measurements : NavItem(
        pathToIcon = "vector/speed_black_24dp.svg",
        title = "Измерения"
    )

    object Parameters : NavItem(
        pathToIcon = "vector/tune_black_24dp.svg",
        title = "Параметры"
    )

    object Norms : NavItem(
        pathToIcon = "vector/rule_black_24dp.svg",
        title = "Нормы"
    )

    object SampleTypes : NavItem(
        pathToIcon = "vector/memory_black_24dp.svg",
        title = "Изделия"
    )

    object Conditions : NavItem(
        pathToIcon = "vector/thermostat_black_24dp.svg",
        title = "Условия"
    )

    object AppSettings : NavItem(
        pathToIcon = "vector/settings_black_24dp.svg",
        title = "Настройки"
    )

    class SampleDetails(val sample: Sample) : NavItem(
        pathToIcon = "vector/memory_black_24dp.svg",
        title = "Образец #${sample.identifier ?: ""}"
    )

    companion object {
        fun getMainNavigationItems() =
            listOf<NavItem>(Samples, Parameters, Operations, Measurements, AppSettings)

        val homeItem = Samples
    }
}
