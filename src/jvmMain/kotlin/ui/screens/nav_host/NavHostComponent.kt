package ui.screens.nav_host

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import domain.*
import domain.application.Result
import domain.application.baseUseCases.GetEntities
import domain.application.baseUseCases.GetEntity
import domain.application.baseUseCases.InsertEntity
import domain.application.baseUseCases.RemoveEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import navigation.NavItem
import org.kodein.di.DI
import org.kodein.di.instance
import ui.screens.norms.NormsComponent
import ui.screens.operationtypes.OperationTypesComponent
import ui.screens.parameters.ParametersComponent
import ui.screens.places.PlacesComponent
import ui.screens.samples.SamplesComponent
import ui.screens.workers.WorkersComponent

/**
 * Main navigation component that holds all destinations
 */
class NavHostComponent constructor(
    private val di: DI,
    componentContext: ComponentContext,
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
            childFactory = ::createChild
        )


    override val state: Value<INavHost.State> = _state

    override val sampleTypes: Value<List<SampleType>> = _sampleTypesState

    override val childStack: Value<ChildStack<*, INavHost.Child>>
        get() = stack

    private fun createChild(config: Config, componentContext: ComponentContext): INavHost.Child {
        return when (config) {
            Config.Places -> INavHost.Child.Places(PlacesComponent(componentContext))
            Config.Workers -> INavHost.Child.Workers(WorkersComponent(componentContext))
            Config.Norms -> INavHost.Child.Norms(NormsComponent(componentContext))
            Config.Parameters -> INavHost.Child.Parameters(ParametersComponent(di = di, componentContext))
            Config.OperationsTypes -> INavHost.Child.Operations(OperationTypesComponent(componentContext))
            Config.Samples -> INavHost.Child.Samples(
                SamplesComponent(
                    di = di,
                    componentContext
                )
            )
        }
    }

    override fun setDestination(navItem: NavItem) {

        val newConf = when (navItem) {
            NavItem.Conditions -> null
            NavItem.Measurements -> null
            NavItem.Norms -> Config.Norms
            NavItem.Operations -> null
            NavItem.Parameters -> Config.Parameters
            NavItem.Places -> Config.Places
            NavItem.SampleTypes -> null
            NavItem.Samples -> Config.Samples
            NavItem.Workers -> Config.Workers
            NavItem.OperationTypes -> Config.OperationsTypes
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
        object OperationsTypes : Config()

        @Parcelize
        object Samples : Config()

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