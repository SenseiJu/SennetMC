package me.senseiju.sennetmc.users

enum class UserTable(val databaseField: String, private val databaseFieldType: String) {
    DAILY_REWARD_LAST_CLAIMED_TIME("daily_reward_last_claimed_time", "BIGINT");

    companion object {
        private val queryValues = "?,".repeat(values().size).removeSuffix(",")
        private val queryFields = values().joinToString { "`${it.databaseField}`" }
        private val queryFieldsWithParameter = values().joinToString { "`${it.databaseField}`=?" }
        private val queryFieldsWithType = values().joinToString { "`${it.databaseField}` ${it.databaseFieldType}" }

        fun buildCreateTableQuery(): String =
                "CREATE TABLE IF NOT EXISTS `users`(`uuid` CHAR(36) NOT NULL, $queryFieldsWithType, UNIQUE(`uuid`));"

        fun buildUpdateTableQuery() : String =
                "INSERT INTO `users`(`uuid`, $queryFields) VALUES(?, ${queryValues}) ON DUPLICATE KEY UPDATE $queryFieldsWithParameter;"
    }
}