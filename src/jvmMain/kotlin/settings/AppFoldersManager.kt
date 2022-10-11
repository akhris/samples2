package settings

import java.nio.file.Path
import kotlin.io.path.*

object AppFoldersManager {
    private val currentUserPath = System.getProperty("user.home")       //"/home/user"
    private val componentsSupPath = ".samples_app"
    private val appPath = Path(currentUserPath, componentsSupPath)      //"/home/user/.components_app"
    private val translationsSubPath = "translations"
    val translationsPath: Path
        get() {
            val path = appPath.resolve(translationsSubPath)
            if (path.notExists()) {
                path.createDirectories()
            }
            return path
        }

    fun getAppPath(): Path {
        if (appPath.notExists())
            appPath.createDirectories()

        return appPath
    }

}