package ui.dialogs.import_from_file

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.MutableValue
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
import ui.screens.base_entity_screen.EntityComponent
import utils.log
import kotlin.reflect.KClass

class ImportFromFileComponent<T : IEntity>(
    private val entityClass: KClass<out T>,
    private val filePath: String,
    private val di: DI,
    componentContext: ComponentContext
) : IImportFromFile<T>, ComponentContext by componentContext {

    private val _state: MutableValue<IImportFromFile.State<T>> =
        MutableValue(IImportFromFile.State(filePath = filePath))

    override val state: Value<IImportFromFile.State<T>> = _state

    init {
        log("initializing import from file component for ${entityClass.simpleName}")
    }

    companion object {
        inline operator fun <reified T : IEntity> invoke(
            filePath: String,
            di: DI,
            componentContext: ComponentContext
        ): ImportFromFileComponent<T> {

            return ImportFromFileComponent(
                entityClass = T::class,
                filePath = filePath,
                di = di,
                componentContext = componentContext
            )
        }
    }

}