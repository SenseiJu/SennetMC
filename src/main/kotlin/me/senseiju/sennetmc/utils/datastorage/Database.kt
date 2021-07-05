package me.senseiju.sennetmc.utils.datastorage

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.senseiju.sentils.storage.ConfigFile
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException
import javax.sql.rowset.CachedRowSet
import javax.sql.rowset.RowSetProvider

class Database(plugin: JavaPlugin, configPath: String) {
    private var source: HikariDataSource

    init {
        val dbConfigFile = ConfigFile(plugin, configPath, true)
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl =
            "jdbc:mysql://${dbConfigFile.getString("host", "localhost")}:${dbConfigFile.getInt("port", 3306)}/${dbConfigFile.getString("database", "{NO_DATABASE_SPECIFIED}")}" +
                    "?autoReconnect=true&allowMultiQueries=true&characterEncoding=utf-8&serverTimezone=UTC&useSSL=false"
        hikariConfig.username = dbConfigFile.getString("username", "root")
        hikariConfig.password = dbConfigFile.getString("password", "root")
        hikariConfig.connectionTimeout = 8000
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true")

        source = HikariDataSource(hikariConfig)
    }

    suspend fun asyncQuery(q: String, vararg replacements: Any = emptyArray()): CachedRowSet {
        return withContext(Dispatchers.IO) {
            query(q, *replacements)
        }
    }

    fun query(q: String, vararg replacements: Any = emptyArray()): CachedRowSet {
        source.connection.use { conn ->
            val s = conn.prepareStatement(q)

            var i = 1

            replacements.forEach { replacement -> s.setObject(i++, replacement) }

            val set = s.executeQuery()

            val cachedSet = RowSetProvider.newFactory().createCachedRowSet()
            cachedSet.populate(set)

            return cachedSet
        }
    }

    suspend fun asyncUpdateQuery(q: String, vararg replacements: Any = emptyArray()) {
        withContext(Dispatchers.IO) {
            updateQuery(q, *replacements)
        }
    }

    fun updateQuery(q: String, vararg replacements: Any = emptyArray()) {
        source.connection.use { conn ->
            val s = conn.prepareStatement(q)

            var i = 1

            replacements.forEach { replacement -> s.setObject(i++, replacement) }

            try {
                s.executeUpdate()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun updateBatchQuery(q: String, vararg replacements: Replacements = emptyArray()) {
        source.connection.use { conn ->
            conn.autoCommit = false

            val s = conn.prepareStatement(q)

            var i = 1

            replacements.forEach { set ->
                set.replacements.forEach { replacement ->
                    s.setObject(i++, replacement)
                }

                s.addBatch()

                i = 1
            }

            try {
                s.executeBatch()
            } catch (ex: SQLException) {
                ex.printStackTrace()
            }

            conn.commit()
            conn.autoCommit = true
        }
    }
}