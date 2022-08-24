package persistence.dto

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object Tables {

    object SampleTypes : UUIDTable() {
        val name = text(name = "name")
        val description = text(name = "description").nullable()
    }

    object Samples : IntIdTable() {
        val sampleID = text(name = "sampleID")
        val type = reference(name = "type", foreign = SampleTypes, onDelete = ReferenceOption.CASCADE)
        val description = text(name = "description").nullable()
        val comment = text(name = "comment").nullable()
        val orderID = text(name = "orderID").nullable()
    }

}