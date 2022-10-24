package persistence.export_import.json.application

import domain.IEntity
import domain.IExportImportRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class ImportFromJSON<ENTITY : IEntity, JSONENTITY>(
    val repo: IExportImportRepository<ENTITY, JSONENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<List<JSONENTITY>, ImportFromJSON.Params>(ioDispatcher) {
    override suspend fun run(params: Params): List<JSONENTITY> {
        return when (params) {
            is Params.ImportFromFile -> {
                repo.import(params.filePath)
            }
        }
    }

    sealed class Params {
        data class ImportFromFile(val filePath: String) : Params()
    }
}