package ui.screens.nav_host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.replaceCurrent
import com.arkivanov.decompose.router.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import domain.entities.*
import domain.entities.fieldsmappers.FieldsMapperFactory
import domain.entities.usecase_factories.*
import navigation.NavItem
import navigation.Screen
import persistence.columnMappers.ColumnMappersFactory
import settings.AppSettingsRepository
import ui.screens.entities_screen.EntitiesScreenComponent
import ui.screens.settings.SettingsComponent

/**
 * Main navigation component that holds all destinations
 */
class NavHostComponent constructor(
    componentContext: ComponentContext,
    private val fieldsMapperFactory: FieldsMapperFactory,
    private val columnMappersFactory: ColumnMappersFactory,
    private val appSettingsRepository: AppSettingsRepository,
    private val getUseCaseFactory: IGetUseCaseFactory,
    private val updateUseCaseFactory: IUpdateUseCaseFactory,
    private val getListUseCaseFactory: IGetListUseCaseFactory,
    private val insertUseCaseFactory: IInsertUseCaseFactory,
    private val removeUseCaseFactory: IRemoveUseCaseFactory
) :
    INavHost, ComponentContext by componentContext {

    /**
     * Router instance
     */
    private val router =
        router(
            initialConfiguration = Config(NavItem.getDefaultHome().route),
            handleBackButton = true, // Pop the back stack on back button press
            childFactory = ::createChild,
            key = "nav_host_router"
        )

    /**
     * Exposes Router State
     */
    override val routerState: Value<RouterState<Config, INavHost.Child>> = router.state

    /**
     * Navigate to destination by route.
     */
    override fun setDestination(route: String) {
        router.replaceCurrent(Config(route))
    }

    /**
     * Child components factory.
     * Creates
     */
    private fun createChild(config: Config, componentContext: ComponentContext): INavHost.Child {
        return when (config.route) {
            Screen.Settings.route -> INavHost.Child.Settings(
                SettingsComponent(
                    componentContext = componentContext,
                    appSettingsRepository = appSettingsRepository
                )
            )
            else -> {
                val entities = when (config.route) {
                    Screen.Warehouse.route -> listOf(WarehouseItem::class)
                    Screen.Income.route -> listOf(ItemIncome::class)
                    Screen.Outcome.route -> listOf(ItemOutcome::class)
                    Screen.Types.route -> listOf(
                        Item::class,
                        ObjectType::class,
                        domain.entities.Unit::class,
                        Parameter::class,
                        Container::class,
                        Supplier::class,
                        Invoice::class,
                        Project::class
                    )
                    else -> throw UnsupportedOperationException("unknown root: ${config.route}")
                }

                INavHost.Child.EntitiesListWithSidePanel(
                    EntitiesScreenComponent(
                        componentContext = componentContext,
                        entityClasses = entities,
                        fieldsMapperFactory = fieldsMapperFactory,
                        columnMappersFactory = columnMappersFactory,
                        getListUseCaseFactory = getListUseCaseFactory,
                        updateUseCaseFactory = updateUseCaseFactory,
                        removeUseCaseFactory = removeUseCaseFactory,
                        insertUseCaseFactory = insertUseCaseFactory
                    )
                )
            }
        }
    }


    @Parcelize
    data class Config(val route: String) : Parcelable

}