package ui.dialogs.import_from_file

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import domain.IEntity
import domain.Measurement
import org.kodein.di.DI
import org.kodein.di.instance
import persistence.export_import.json.application.ImportFromJSON
import persistence.export_import.json.dto.JSONMeasurement
import ui.dialogs.import_from_file.import_measurements.ImportMeasurementsComponent
import kotlin.reflect.KClass

class ImportFromFileComponent(
    private val entityClass: KClass<out IEntity>,
    private val filePath: String,
    private val di: DI,
    componentContext: ComponentContext
) : IImportFromFile, ComponentContext by componentContext {


    private val dialogNav = StackNavigation<DialogConfig>()

    private val _dialogStack = childStack(
        source = dialogNav,
        initialConfiguration = DialogConfig.None,
//            handleBackButton = true,
        childFactory = ::createChild,
        key = "import entity dialog stack"
    )

    override val dialogStack: Value<ChildStack<*, IImportFromFile.ImportEntity>> = _dialogStack

    private fun createChild(config: DialogConfig, componentContext: ComponentContext): IImportFromFile.ImportEntity {
        return when (config) {
            DialogConfig.None -> IImportFromFile.ImportEntity.None
            DialogConfig.Measurements -> IImportFromFile.ImportEntity.Measurements(
                component = ImportMeasurementsComponent(
                    filePath = filePath,
                    di = di,
                    componentContext = componentContext
                )
            )
        }
    }


    @Parcelize
    sealed class DialogConfig : Parcelable {
        @Parcelize
        object None : DialogConfig()

        @Parcelize
        object Measurements : DialogConfig()
    }


    init {
        when (entityClass) {
            Measurement::class -> {
                dialogNav.replaceCurrent(DialogConfig.Measurements)
            }
        }
    }
}