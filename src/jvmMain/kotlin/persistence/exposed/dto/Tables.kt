package persistence.exposed.dto

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime
import persistence.Column

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
    object Samples : UUIDTable() {
        val sampleID = text(name = "sampleID").nullable()
        val sampleType =
            reference(name = Column.SampleType.columnName, foreign = SampleTypes, onDelete = ReferenceOption.CASCADE)
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
        val sampleType =
            reference(name = Column.SampleType.columnName, foreign = SampleTypes, onDelete = ReferenceOption.CASCADE)
        val description = text(name = "description").nullable()
        val position = integer(name = "position").nullable()
        val unit = reference(name = "unit", foreign = Units).nullable()
        val factor = integer(name = "factor").nullable()
    }

    object Places : UUIDTable() {
        val name = text(name = "name").nullable()
        val roomNumber = text(name = "roomNumber")
        val description = text(name = "description").nullable()
    }

    object Workers : UUIDTable() {
        val name = text(name = "name")
        val middleName = text(name = "middleName")
        val surname = text(name = "surname")
        val room = reference(name = "room", foreign = Places, onDelete = ReferenceOption.CASCADE).nullable()
        val phoneNumber = text(name = "phoneNumber")
        val email = text(name = "email")
    }


    /**
     * Operation types table: information about operations that can be applied to samples
     */
    object OperationTypes : UUIDTable() {
        val name = text(name = "name")
        val description = text(name = "description").nullable()
    }

    /**
     * Operations table: OperationType with additional info
     */
    object Operations : UUIDTable() {
        val sample = reference(name = "sample", foreign = Samples, onDelete = ReferenceOption.CASCADE).nullable()
        val sampleType =
            reference(name = Column.SampleType.columnName, foreign = SampleTypes, onDelete = ReferenceOption.CASCADE)
        val operationType =
            reference(name = "operationType", foreign = OperationTypes, onDelete = ReferenceOption.CASCADE).nullable()
        val dateTime = datetime(name = "dateTime").nullable()
        val worker = reference(name = "worker", foreign = Workers, onDelete = ReferenceOption.CASCADE).nullable()
        val place = reference(name = "place", foreign = Places, onDelete = ReferenceOption.CASCADE).nullable()
    }

    object Units : UUIDTable() {
        val unit = text(name = "unit")
        val isMultipliable = bool(name = "isMultipliable").default(false)
    }

    object Measurements : UUIDTable() {
        val sample = reference(name = "sample", foreign = Samples, onDelete = ReferenceOption.CASCADE).nullable()
        val dateTime = datetime(name = "dateTime").nullable()
        val operator = reference(name = "worker", foreign = Workers, onDelete = ReferenceOption.CASCADE).nullable()
        val place = reference(name = "place", foreign = Places, onDelete = ReferenceOption.CASCADE).nullable()
        val comment = text(name = "comment").nullable()
        val conditions = text(name = "conditions").nullable()
    }

    object MeasurementResults : IntIdTable() {
        val measurement = reference(name = "measurement", foreign = Measurements, onDelete = ReferenceOption.CASCADE)
        val parameter = reference(name = "parameters", foreign = Parameters, onDelete = ReferenceOption.CASCADE)
        val value = text(name = "value").nullable()
    }
}
