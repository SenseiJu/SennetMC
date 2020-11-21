package me.senseiju.commscraft.datastorage

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.senseiju.commscraft.CommsCraft
import javax.sql.rowset.CachedRowSet
import javax.sql.rowset.RowSetProvider

class Database(plugin: CommsCraft, configPath: String) {
    private var source: HikariDataSource

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
        updateQuery("CREATE TABLE IF NOT EXISTS `users`(`uuid` CHAR(36) NOT NULL, `fish_capacity_upgrades` INT, " +
                "`speedboat_upgrades` INT, UNIQUE(`uuid`));")

        updateQuery("CREATE TABLE IF NOT EXISTS `fish_caught`(`uuid` CHAR(36) NOT NULL, `fish_type` CHAR(255) NOT NULL, " +
                "`current` INT, `total` INT, UNIQUE KEY `key_uuid_fish_type`(`uuid`, `fish_type`));")

        updateQuery("CREATE TABLE IF NOT EXISTS `collectables`(`uuid` CHAR(36) NOT NULL, `collectable_id` CHAR(255) NOT NULL, " +
                "UNIQUE KEY `key_uuid_collectable_id`(`uuid`, `collectable_id`));")
    }

    suspend fun asyncQuery(q: String, vararg replacements: Any = emptyArray()): CachedRowSet {
        return withContext(Dispatchers.IO) {
            query(q, *replacements)
        }
    }

    fun query(q: String, vararg replacements: Any = emptyArray()): CachedRowSet {
        source.connection.use {
            val s = it.prepareStatement(q)

            var i = 1

            for (replacement in replacements) {
                s.setObject(i++, replacement)
            }

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
        source.connection.use {
            val s = it.prepareStatement(q)

            var i = 1
            for (replacement in replacements) {
                s.setObject(i++, replacement)
            }

            try {
                s.executeUpdate()
            } catch (ex: Exception) {}
        }
    }
}