package domain

import java.io.Serializable


interface IExportImportRepository<ENTITY : IEntity, JSONENTITY> {
    suspend fun export(filePath: String, entities: List<ENTITY>)
    suspend fun import(filePath: String): List<JSONENTITY>
}