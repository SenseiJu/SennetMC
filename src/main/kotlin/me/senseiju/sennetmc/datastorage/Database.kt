package me.senseiju.sennetmc.datastorage

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.senseiju.sennetmc.SennetMC
import me.senseiju.sennetmc.settings.Setting
import me.senseiju.sennetmc.upgrades.Upgrade
import me.senseiju.sennetmc.users.UserTable
import javax.sql.rowset.CachedRowSet
import javax.sql.rowset.RowSetProvider

class Database(plugin: SennetMC, configPath: String) {
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
        updateQuery(UserTable.buildCreateTableQuery())

        updateQuery("CREATE TABLE IF NOT EXISTS `models`(`uuid` CHAR(36) NOT NULL, `model_type` CHAR(255) NOT NULL, " +
                "`model_data` INT NOT NULL, UNIQUE KEY `key_uuid_model`(`uuid`, `model_type`, `model_data`));")

        updateQuery("CREATE TABLE IF NOT EXISTS `active_models`(`uuid` CHAR(36) NOT NULL, `model_type` CHAR(255) NOT NULL, " +
                "`model_data` INT NOT NULL, UNIQUE KEY `key_uuid_model_type`(`uuid`, `model_type`));")

        updateQuery(Setting.buildCreateTableQuery())

        updateQuery(Upgrade.buildCreateTableQuery())

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

    fun updateBatchQuery(q: String, vararg replacementSets: ReplacementSet = emptyArray()) {
        source.connection.use { conn ->
            conn.autoCommit = false

            val s = conn.prepareStatement(q)

            var i = 1

            replacementSets.forEach { set ->
                set.replacements.forEach { replacement ->
                    s.setObject(i++, replacement)
                }

                s.addBatch()

                i = 1
            }

            try {
                s.executeBatch()

                conn.commit()
                conn.autoCommit = true
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}