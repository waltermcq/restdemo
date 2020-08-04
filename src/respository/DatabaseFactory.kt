package com.wam.respository

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    /*
    Create and configure Hikari connection pooler with H2
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
            TODO("SchemaUtils.create() tables")
            TODO("create some init / test data here")
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