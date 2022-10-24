package persistence.export_import.json.application

import domain.IEntity
import domain.IExportImportRepository
import domain.IRepository
import domain.application.IoDispatcher
import domain.application.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class ExportToJSON<ENTITY : IEntity, JSONENTITY>(
    val repo: IExportImportRepository<ENTITY, JSONENTITY>,
    @IoDispatcher
    ioDispatcher: CoroutineDispatcher
) : UseCase<Unit, ExportToJSON.Params<ENTITY>>(ioDispatcher) {


    override suspend fun run(params: Params<ENTITY>) {
        when (params) {
            is Params.ExportToFile -> {
                repo.export(params.filePath, params.entities)
            }
        }
    }

    sealed class Params<ENTITY : IEntity> {
        data class ExportToFile<ENTITY : IEntity>(val filePath: String, val entities: List<ENTITY>) : Params<ENTITY>()
    }
}