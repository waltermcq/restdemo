package com.wam.respository

import com.wam.model.Contact
import com.wam.model.Contacts
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    /*
    Create and configure Hikari connection pooling with H2
     */
    private fun createHikariDataSource(): HikariDataSource {
        val dbConfig = HikariConfig()
        dbConfig.driverClassName = "org.h2.Driver"
        dbConfig.jdbcUrl = "jdbc:h2:mem:test" // considering using environment variable?
        dbConfig.maximumPoolSize = 3
        dbConfig.isAutoCommit = false
        dbConfig.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        dbConfig.validate()
        return HikariDataSource(dbConfig)
    }

    fun initDatabase() {
        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(Contacts)

            // create init / test data here
            Contacts.insert {
                it[firstName] = "john"
                it[middleName] = "tester"
                it[lastName] = "smith"
                it[street] = "500 boylston street"
                it[city] = "boston"
                it[state] = "MA"
                it[zip] = "02116"
                it[phoneHome] = "6173732210"
                it[phoneMobile] = "9999999999"
                it[phoneWork] = "2222222222"
            }

        }
    }

    /*
    Helper function to wrap queries/transactions with Dispatchers.IO coroutine dispatcher for async
     */
    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO) {
            transaction { block() }
        }
}