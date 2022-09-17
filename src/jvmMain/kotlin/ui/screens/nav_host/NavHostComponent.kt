package ui.screens.nav_host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.lifecycle.subscribe
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.RemoveEntity
import kotlinx.coroutines.*
import navigation.NavItem
import org.kodein.di.DI
import org.kodein.di.instance
import ui.screens.base_entity_screen.EntityComponent
import ui.screens.measurements.MeasurementsComponent

/**
 * Main navigation component that holds all destinations
 */
class NavHostComponent constructor(
    private val di: DI,
    componentContext: ComponentContext
) :
    INavHost, ComponentContext by componentContext {

    private val scope =
        CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _state = MutableValue(INavHost.State())
    private val _sampleTypesState = MutableValue(listOf<SampleType>())

    private val getSampleTypes: GetEntities<SampleType> by di.instance()
    private val insertSampleType: InsertEntity<SampleType> by di.instance()
    private val removeSampleType: RemoveEntity<SampleType> by di.instance()

    private val samplesCallback: IRepositoryCallback<SampleType> by di.instance()
    private val navigation = StackNavigation<Config>()

    private val stack =
        childStack(
            source = navigation,
            initialConfiguration = Config.Samples,
            handleBackButton = true,
            childFactory = ::createChild,
            key = "nav host stack"
        )


    override val state: Value<INavHost.State> = _state

    override val sampleTypes: Value<List<SampleType>> = _sampleTypesState

    override val childStack: Value<ChildStack<*, INavHost.Child>>
        get() = stack

    private fun createChild(config: Config, componentContext: ComponentContext): INavHost.Child {
        return when (config) {
            Config.Places -> INavHost.Child.Places(EntityComponent(di = di, componentContext))
            Config.Workers -> INavHost.Child.Workers(EntityComponent(di = di, componentContext))
            Config.Norms -> INavHost.Child.Norms(EntityComponent(di = di, componentContext))
            Config.Parameters -> INavHost.Child.Parameters(EntityComponent(di = di, componentContext))
            Config.Operations -> INavHost.Child.Operations(EntityComponent(di = di, componentContext))
            Config.Samples -> INavHost.Child.Samples(EntityComponent(di = di, componentContext))
            Config.OperationTypes -> INavHost.Child.OperationTypes(EntityComponent(di = di, componentContext))
            Config.Measurements -> INavHost.Child.Measurements(
                MeasurementsComponent(
                    di = di,
                    componentContext = componentContext
                )
            )
        }
    }


    override fun setDestination(navItem: NavItem) {

        val newConf = when (navItem) {
            NavItem.Conditions -> null
            NavItem.Measurements -> Config.Measurements
            NavItem.Norms -> Config.Norms
            NavItem.Operations -> Config.Operations
            NavItem.Parameters -> Config.Parameters
            NavItem.Places -> Config.Places
            NavItem.SampleTypes -> null
            NavItem.Samples -> Config.Samples
            NavItem.Workers -> Config.Workers
            NavItem.OperationTypes -> Config.OperationTypes
            NavItem.AppSettings -> null
        }
        if (newConf != null && navItem != _state.value.currentDestination) {
            navigation.replaceCurrent(newConf)
        }

        _state.reduce {
            it.copy(currentDestination = navItem)
        }
    }

    @Parcelize
    private sealed class Config : Parcelable {
        @Parcelize
        object Places : Config()

        @Parcelize
        object Workers : Config()

        @Parcelize
        object Norms : Config()

        @Parcelize
        object Parameters : Config()

        @Parcelize
        object Operations : Config()

        @Parcelize
        object OperationTypes : Config()

        @Parcelize
        object Samples : Config()

        @Parcelize
        object Measurements : Config()

    }


    override fun addSampleType(type: SampleType) {
        scope.launch {
            insertSampleType(InsertEntity.Insert(type))
        }
    }

    override fun removeSampleType(type: SampleType) {
        scope.launch {
            removeSampleType(RemoveEntity.Remove(type))
        }
    }

    private suspend fun invalidateSampleTypes() {
        val types = getSampleTypes(GetEntities.Params.GetWithSpecification(Specification.QueryAll))
        when (types) {
            is Result.Failure -> {}
            is Result.Success -> {
                when (types.value) {
                    is EntitiesList.Grouped -> {}
                    is EntitiesList.NotGrouped -> {
                        _sampleTypesState.reduce { types.value.items }
                    }
                }

            }
        }
    }

    init {
        lifecycle.subscribe(onDestroy = {
            scope.coroutineContext.cancelChildren()
        })

        scope.launch {
            invalidateSampleTypes()
        }

        scope.launch {
            samplesCallback
                .updates
                .collect {
                    when (it) {
                        is RepoResult.ItemInserted,
                        is RepoResult.ItemRemoved,
                        is RepoResult.ItemUpdated -> {
                            invalidateSampleTypes()
                        }
                    }
                }
        }
    }

}