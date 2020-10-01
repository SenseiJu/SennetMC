package me.senseiju.commscraft.datastorage

import com.sun.rowset.CachedRowSetImpl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.senseiju.commscraft.CommsCraft
import java.util.*

class Database(plugin: CommsCraft, configPath: String) {
    var source: HikariDataSource

    init {
        val file = DataFile(plugin, configPath, true)
        val config = file.config
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl =
                "jdbc:mysql://${config.getString("host")}:${config.getInt("port")}/${config.getString("database")}" +
                        "?autoReconnect=true&allowMultiQueries=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false"
        hikariConfig.username = config.getString("username")
        hikariConfig.password = config.getString("password")
        hikariConfig.connectionTimeout = 8000
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")

        source = HikariDataSource(hikariConfig)

        createTables()
    }

    private fun createTables() {
        updateQuery("CREATE TABLE IF NOT EXISTS `users`(`uuid` CHAR(36));")
        updateQuery("CREATE TABLE IF NOT EXISTS `collectables`(`uuid` CHAR(36), `collectable_id` TEXT);")
    }

    suspend fun asyncQuery(q: String, vararg replacements: String = emptyArray()): CachedRowSetImpl {
        return withContext(Dispatchers.IO) {
            query(q, *replacements)
        }
    }

    fun query(q: String, vararg replacements: String = emptyArray()): CachedRowSetImpl {
        source.connection.use {
            val s = it.prepareStatement(q)

            var i = 1

            for (replacement in replacements) {
                s.setString(i++, replacement)
            }

            val set = s.executeQuery()

            val cachedSet = CachedRowSetImpl()
            cachedSet.populate(set)

            return cachedSet
        }
    }

    suspend fun asyncUpdateQuery(q: String, vararg replacements: String = emptyArray()) {
        withContext(Dispatchers.IO) {
            updateQuery(q, *replacements)
        }
    }

    fun updateQuery(q: String, vararg replacements: String = emptyArray()) {
        source.connection.use {
            val s = it.prepareStatement(q)

            var i = 1
            for (replacement in replacements) {
                s.setObject(i++, replacement)
            }

            s.executeUpdate()
        }
    }
}