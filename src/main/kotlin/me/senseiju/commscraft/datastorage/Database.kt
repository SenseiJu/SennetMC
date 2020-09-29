package me.senseiju.commscraft.datastorage

import com.sun.rowset.CachedRowSetImpl
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.senseiju.commscraft.CommsCraft
import me.senseiju.commscraft.utils.mainScope

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
        mainScope.launch {
            performUpdateQuery("CREATE TABLE IF NOT EXISTS `table_name`(`columns` TEXT);")
        }
    }

    suspend fun performQuery(q: String, vararg replacements: Any = emptyArray()) : CachedRowSetImpl {
        return withContext(Dispatchers.IO) {
            with(source.connection) {
                val s = prepareStatement(q)

                var i = 1
                for (replacement in replacements) {
                    s.setObject(i++, replacement)
                }

                val set = s.executeQuery()

                val cachedSet = CachedRowSetImpl()
                cachedSet.populate(set)

                return@with cachedSet
            }
        }
    }

    suspend fun performUpdateQuery(q: String, vararg replacements: Any = emptyArray()) {
        withContext(Dispatchers.IO) {
            with(source.connection) {
                val s = prepareStatement(q)

                var i = 1
                for (replacement in replacements) {
                    s.setObject(i++, replacement)
                }

                s.executeUpdate()
            }
        }
    }
}