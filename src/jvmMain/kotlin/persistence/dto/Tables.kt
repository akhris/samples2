package persistence.dto

import org.jetbrains.exposed.dao.id.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

object Tables {

    /**
     * Sample Types table - contains just id, name and description of sample type.
     */
    object SampleTypes : UUIDTable() {
        val name = text(name = "name")
        val description = text(name = "description").nullable()
    }

    /**
     * Samples table: base information about samples.
     */
    object Samples : IntIdTable() {
        val sampleID = text(name = "sampleID")
        val type = reference(name = "type", foreign = SampleTypes, onDelete = ReferenceOption.CASCADE)
        val description = text(name = "description").nullable()
        val comment = text(name = "comment").nullable()
        val orderID = text(name = "orderID").nullable()
    }

    /**
     * Parameters table: information about parameters and their positions in the measurements list
     * id here is not auto-incremented
     */
    object Parameters : UUIDTable() {
        val name = text(name = "name")
        val sampleType = reference(name = "sample_type", foreign = SampleTypes, onDelete = ReferenceOption.CASCADE)
        val description = text(name = "description").nullable()
        val position = integer(name = "position").nullable()
    }

}
