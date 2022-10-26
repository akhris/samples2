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
import ui.dialogs.import_from_file.import_measurements.ImportMeasurementsComponent
import ui.screens.base_entity_screen.entityComponents.FileExtensions
import utils.log
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.isDirectory
import kotlin.reflect.KClass

class ImportFromFileComponent<T : IEntity>(
    private val entityClass: KClass<out T>,
    private val filePath: String,
    private val di: DI,
    componentContext: ComponentContext
) : IImportFromFile<T>, ComponentContext by componentContext {


    private val nav = StackNavigation<Config>()

    private val _stack = childStack(
        source = nav,
        initialConfiguration = Config.None,
        childFactory = ::createChild,
        key = "import entity stack"
    )

    override val stack: Value<ChildStack<*, IImportFromFile.ImportDialog>> = _stack
    private fun createChild(config: Config, componentContext: ComponentContext): IImportFromFile.ImportDialog {
        return when (config) {
            Config.Import -> {
                when (entityClass) {
                    Measurement::class -> IImportFromFile.ImportDialog.ImportMeasurementsDialog(
                        ImportMeasurementsComponent(
                            filePath = filePath, di = di, componentContext = componentContext
                        )
                    )

                    else -> throw IllegalArgumentException("class $entityClass is not supported for import")
                }
            }

            Config.None -> IImportFromFile.ImportDialog.None
        }
    }

    private val _state: MutableValue<IImportFromFile.State<T>> =
        MutableValue(IImportFromFile.State(filePath = filePath))

    override val state: Value<IImportFromFile.State<T>> = _state


    @Parcelize
    private sealed class Config : Parcelable {

        @Parcelize
        object Import : Config()

        //todo add class ImportFromXLS, ...
        @Parcelize
        object None : Config()

    }

    private fun checkFile(): Boolean {
        val file = Path(filePath)
        return !(!file.exists() || file.isDirectory())

    }

    private fun initImportConfig() {
        val file = Path(filePath)
        if (!checkFile()) return
        when (file.extension) {
            in FileExtensions.JSON.extensions,
            in FileExtensions.EXCEL.extensions -> {
                nav.replaceCurrent(Config.Import)
            }

            else -> {
                throw IllegalArgumentException("Cannot import from file with extension: ${file.extension}")
            }
        }
    }

    init {
        initImportConfig()
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