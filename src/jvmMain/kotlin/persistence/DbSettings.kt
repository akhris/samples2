package persistence

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import persistence.dto.Tables
import settings.AppFoldersManager
import kotlin.io.path.pathString

object DbSettings {


    val db by lazy {

        val db =
            Database.connect(
            "jdbc:sqlite:${AppFoldersManager.getAppPath().pathString}/data.db?foreign_keys=on",
            "org.sqlite.JDBC"
        )
//            Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared&foreign_keys=on", "org.sqlite.JDBC")
        transaction {
            addLogger(StdOutSqlLogger)

            SchemaUtils.create(
                Tables.SampleTypes,
                Tables.Samples,
                Tables.Parameters
            )
        }
        db
    }

    fun connectToDB(path: String): Database {
        val db = Database.connect("jdbc:sqlite:${path}?foreign_keys=on", "org.sqlite.JDBC")
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(
                Tables.Samples,
                Tables.SampleTypes
            )
        }
        return db
    }
}

/*
foreign_keys=on
https://github.com/JetBrains/Exposed/issues/822
// In file
Database.connect("jdbc:sqlite:/data/data.db", "org.sqlite.JDBC")
// In memory
Database.connect("jdbc:sqlite:file:test?mode=memory&cache=shared", "org.sqlite.JDBC")
// For both: set SQLite compatible isolation level, see
// https://github.com/JetBrains/Exposed/wiki/FAQ
TransactionManager.manager.defaultIsolationLevel =
    Connection.TRANSACTION_SERIALIZABLE
    // or Connection.TRANSACTION_READ_UNCOMMITTED
//Gradle
implementation("org.xerial:sqlite-jdbc:3.30.1")


 */