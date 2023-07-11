package com.example.android_sdk

import java.sql.*
import java.util.*

object ConfigHolder {
    lateinit var databaseUrl: String
    lateinit var databaseUsername: String
    lateinit var databasePassword: String
    lateinit var databaseDriverClassName: String
}

fun setConfiguration(databaseUrl: String, databaseUsername: String, databasePassword: String, databaseDriverClassName: String) {
    ConfigHolder.databaseUrl = databaseUrl
    ConfigHolder.databaseUsername = databaseUsername
    ConfigHolder.databasePassword = databasePassword
    ConfigHolder.databaseDriverClassName = databaseDriverClassName
}

class DBConnector() {
    // Access the configuration values from MyOtherClass or any other class
    var databaseUrl = ConfigHolder.databaseUrl
    var databaseUsername = ConfigHolder.databaseUsername
    var databasePassword = ConfigHolder.databasePassword
    var databaseDriverClassName = ConfigHolder.databaseDriverClassName

    private var connection: Connection? = null

    fun connect() {
        try {
            if (databaseUrl == null) {
                val properties = Properties()
                val configFile = javaClass.classLoader.getResourceAsStream("config.properties")
                properties.load(configFile)
                setConfiguration(properties.getProperty("databaseUrl"),
                    properties.getProperty("databaseUsername"),
                    properties.getProperty("databasePassword"),
                    properties.getProperty("databaseDriverClassName"))
                databaseUrl = ConfigHolder.databaseUrl
                databaseUsername = ConfigHolder.databaseUsername
                databasePassword = ConfigHolder.databasePassword
                databaseDriverClassName = ConfigHolder.databaseDriverClassName
            }
            Class.forName(databaseDriverClassName)
            connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword)
            println("Database Connection Successful")
        } catch (ex: ClassNotFoundException) {
            ex.printStackTrace()
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            connection?.close()
            println("Database Connection Closed")
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }
    }

    fun getConnection(): Connection? {
        return connection
    }
}

class DBQueryExector(private val connection: Connection){
    fun executeQuery(sqlQuery : String) : ResultSet?{
        var statement : Statement? = null
        var resultSet : ResultSet? = null

        try{
            statement = connection.createStatement()
            resultSet = statement.executeQuery(sqlQuery)
            return resultSet
        } catch (ex : SQLException){
            ex.printStackTrace()
        } finally {
            statement?.close()
        }

        return null
    }
}
