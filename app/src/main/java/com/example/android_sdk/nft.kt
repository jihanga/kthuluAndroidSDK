package com.example.android_sdk

import android.annotation.SuppressLint
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Bool
import org.web3j.abi.datatypes.DynamicArray
import org.web3j.abi.datatypes.DynamicBytes
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Utf8String
import org.web3j.abi.datatypes.generated.Bytes4
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.abi.datatypes.generated.Uint8
import org.web3j.crypto.Credentials
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Numeric
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.HttpURLConnection
import java.net.URL
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.Date

suspend fun getMintableAddress(
    owner: Array<String>
): JSONObject = withContext(Dispatchers.IO) {

    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val CAArray = JSONArray()
    val CAData = JSONObject()

    val own = owner?.joinToString("','", "'", "'")

    val CAQuery=
        "SELECT " +
                "network, " +
                "collection_id, " +
                "collection_name, " +
                "collection_symbol, " +
                "nft_type, " +
                "creator, " +
                "owner, " +
                "total_supply, " +
                "deployment_date, " +
                "slug, " +
                "category, "+
                "logo_url, "+
                "image_s3_url, "+
                "isverified, "+
                "numOwners, "+
                "currency, "+
                "discord_link, "+
                "twitter_link, "+
                "instagram_link, "+
                "facebook_link, "+
                "telegram_link, "+
                "external_url "+
                "FROM " +
                "nft_collection_table " +
                "WHERE " +
                "network IN ('ethereum','cypress','polygon','bnb') " +
                "AND " +
                "creator IN ('0x780A19638D126d59f4Ed048Ae1e0DC77DAf39a77','0x7E055Cb85FBE64da619865Df8a392d12f009aD81')" +
                "AND " +
                " owner IN (${own})"

    try {
        if (connection != null) {
            val dbQueryExector = DBQueryExector(connection)
            val getCA: ResultSet? = dbQueryExector.executeQuery(CAQuery)

            if (getCA != null) {
                try {
                    while (getCA.next()) {
                        val objRes = JSONObject()

                        val network = getCA.getString("network")
                        val collection_id = getCA.getString("collection_id")
                        val collection_name = getCA.getString("collection_name")
                        val collection_symbol = getCA.getString("collection_symbol")
                        val nft_type = getCA.getString("nft_type")
                        val creator = getCA.getString("creator")
                        val owner = getCA.getString("owner")
                        val total_supply = getCA.getString("total_supply")
                        val deployment_date = getCA.getInt("deployment_date")
                        val slug = getCA.getString("slug")
                        val category = getCA.getString("category")
                        val logo_url = getCA.getString("logo_url")
                        val image_s3_url = getCA.getString("image_s3_url")
                        val isverified = getCA.getString("isverified")
                        val numOwners = getCA.getInt("numOwners")
                        val currency = getCA.getString("currency")
                        val discord_link = getCA.getString("discord_link")
                        val twitter_link = getCA.getString("twitter_link")
                        val instagram_link = getCA.getString("instagram_link")
                        val facebook_link = getCA.getString("facebook_link")
                        val telegram_link = getCA.getString("telegram_link")
                        val external_url = getCA.getString("external_url")

                        objRes.put("network", network)
                        objRes.put("collection_id", collection_id)
                        objRes.put("collection_name", collection_name)
                        objRes.put("collection_symbol", collection_symbol)
                        objRes.put("nft_type", nft_type)
                        objRes.put("creator", creator)
                        objRes.put("owner", owner)
                        objRes.put("total_supply", total_supply)
                        objRes.put("deployment_date", deployment_date)
                        objRes.put("slug", slug)
                        objRes.put("category", category)
                        objRes.put("logo_url", logo_url)
                        objRes.put("image_s3_url", image_s3_url)
                        objRes.put("isverified", isverified)
                        objRes.put("numOwners", numOwners)
                        objRes.put("currency", currency)
                        objRes.put("discord_link", discord_link)
                        objRes.put("twitter_link", twitter_link)
                        objRes.put("instagram_link", instagram_link)
                        objRes.put("facebook_link", facebook_link)
                        objRes.put("telegram_link", telegram_link)
                        objRes.put("external_url", external_url)

                        CAArray.put(objRes)
                    }
                }
                catch (ex: SQLException){
                    ex.printStackTrace()
                }
                finally {
                    getCA.close()
                }
            }
        }
        dbConnector.disconnect()

        CAData.put("result", "OK")
        CAData.put("value", CAArray)
    }catch (e: Exception){
        CAData.put("result", "FAIL")
        CAData.put("reason", e)
    }
}

suspend fun setNFTsHide(
    network: String,
    account: String,
    collection_id: String,
    token_id: String
): JSONObject = withContext(Dispatchers.IO) {
    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val hideData = JSONObject()

    try {
        val insertQuery =
            "INSERT INTO " +
                    "nft_hide_table (network, account, collection_id, token_id, image_url, nft_name) " +
                    "SELECT " +
                    "'${network}', " +
                    "'${account}', " +
                    "'${collection_id}', " +
                    "'${token_id}', " +
                    "token.image_url AS image_url, " +
                    "token.nft_name AS nft_name " +
                    "FROM " +
                    "nft_token_table AS token " +
                    "WHERE " +
                    "token.network = '${network}' " +
                    "AND " +
                    "token.collection_id = '${collection_id}' " +
                    "AND " +
                    "token.token_id = '${token_id}'"

        println(insertQuery)

        val statement: Statement = connection!!.createStatement()
        statement.executeUpdate(insertQuery)
        hideData.put("result", "OK")
    } catch (e: SQLException) {
        e.printStackTrace()
        hideData.put("result", "FAIL")
        if (e.message?.contains("Duplicate entry") == true) {
            hideData.put("reason", "Duplicate entry")
        }
        else if (e.message?.contains("Table not found") == true) {
            hideData.put("reason", "Table not found")
        }
        else if (e.message?.contains("Column not found") == true) {
            hideData.put("reason", "Column not found")
        }
        else if (e.message?.contains("Connection timeout") == true) {
            hideData.put("reason", "Connection timeout")
        }
        else if (e.message?.contains("Connection refused") == true) {
            hideData.put("reason", "Connection refused")
        }
        else {
            hideData.put("reason", e.printStackTrace())
        }
    } finally {
        connection?.close()
    }
    hideData
}
suspend fun deleteNFTsHide(
    network: String,
    account: String,
    collection_id: String,
    token_id: String
): JSONObject = withContext(Dispatchers.IO) {
    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val hideData = JSONObject()

    try {
        val deleteQuery =
            "DELETE FROM " +
                    "nft_hide_table " +
                    "WHERE " +
                    "network = '$network' " +
                    "AND " +
                    "account = '$account' " +
                    "AND " +
                    "collection_id = '$collection_id' " +
                    "AND " +
                    "token_id = '$token_id' "

        println(deleteQuery)

        val statement: Statement = connection!!.createStatement()
        val rowsAffected = statement.executeUpdate(deleteQuery)

        if (rowsAffected > 0) {
            hideData.put("result", "OK")
        } else {
            hideData.put("result", "FAIL1")
        }
    } catch (e: SQLException) {
        e.printStackTrace()
        hideData.put("result", "FAIL2")
    } finally {
        connection?.close()
    }
    hideData
}

@SuppressLint("SuspiciousIndentation")
suspend fun getNFTsByWallet(
    network: Array<String>,
    account: String ?= null,
    collection_id: String ?= null,
    sort: String ?= null,
    limit: Int ?= null,
    page_number: Int ?= null
): JSONObject = withContext(Dispatchers.IO) {

    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val nftArray = JSONArray() // { ..., value : [ nftArray ]}
    val nftData = JSONObject() // { "result": OK, "sum": 1, "sort": "desc", "page_count": 1, "value" : nftArray }

    val net = network.joinToString("','", "'", "'")

//    var offset = limit?.let { (page_number?.minus(1))?.times(it) }
    var offset = if (page_number != null && limit != null) {
        (page_number - 1) * limit
    } else {
        0 // 또는 적절한 기본값 설정
    }
    if(page_number==null || page_number==0 || page_number==1){
        offset = 0
    }
    var strQuery =
        "SELECT" +
                " owner.network AS network," +
                " collection.collection_id AS collection_id," +
                " collection.collection_name AS collection_name," +
                " collection.collection_symbol AS collection_symbol," +
                " collection.creator AS creator," +
                " collection.deployment_date AS deployment_date," +
                " collection.total_supply AS total_supply," +
                " token.nft_type AS nft_type," +
                " token.minted_time AS minted_time," +
                " token.block_number AS block_number," +
                " owner.owner_account AS owner_account," +
                " token.token_id AS token_id," +
                " owner.balance AS balance," +
                " token.token_uri AS token_uri," +
                " token.nft_name AS nft_name," +
                " token.description AS description," +
                " token.image_url AS image_url," +
                " token.external_url AS external_url," +
                " token.attribute AS attribute," +
                " token.token_info AS token_info" +
                " FROM " +
                "nft_owner_table AS owner" +
                " JOIN " +
                "nft_token_table AS token" +
                " ON" +
                " owner.collection_id = token.collection_id" +
                " AND " +
                " owner.token_id = token.token_id" +
                " AND " +
                " owner.network = token.network" +
                " JOIN " +
                "nft_collection_table AS collection" +
                " ON" +
                " token.collection_id = collection.collection_id" +
                " AND" +
                " token.network = collection.network" +
                " WHERE" +
                " owner.network IN (${net})" +
                " AND" +
                " owner.balance != '0'"
    if (account != null) {
        strQuery += " AND owner.owner_account = '$account'"
    }
    if (collection_id != null) {
        strQuery += " AND owner.collection_id = '$collection_id'"
    }
    strQuery += " AND NOT EXISTS ( SELECT 1 FROM nft_hide_table AS hide WHERE hide.network = owner.network AND hide.account = owner.owner_account AND hide.token_id = owner.token_id AND hide.collection_id = owner.collection_id)"
    strQuery += " ORDER BY token.block_number"
    if (sort == " asc") {
        strQuery += " asc"
    } else {
        strQuery += " desc"
    }
    strQuery += ", CAST(token.token_id AS SIGNED) desc"
    if (limit != null) {
        strQuery += " LIMIT $limit OFFSET $offset"
    }
    println(strQuery)

    var sumQuery =
        "SELECT" +
                " count(*) AS sum" +
                " FROM" +
                " nft_owner_table AS owner" +
                " JOIN" +
                " nft_token_table AS token" +
                " ON" +
                " owner.collection_id = token.collection_id" +
                " AND" +
                " owner.token_id = token.token_id" +
                " AND" +
                " owner.network = token.network" +
                " JOIN" +
                " nft_collection_table AS collection" +
                " ON" +
                " token.collection_id = collection.collection_id" +
                " AND" +
                " token.network = collection.network" +
                " WHERE" +
                " owner.network IN ($net)" +
                " AND" +
                " owner.balance != '0'"
    if (account != null) {
        sumQuery += " AND owner.owner_account = '$account' "
    }
    if (collection_id != null) {
        sumQuery += " AND owner.collection_id = '$collection_id' "
    }
    sumQuery += " AND NOT EXISTS ( SELECT 1 FROM nft_hide_table AS hideWHERE hide.network = owner.network AND hide.account = owner.owner_account AND hide.token_id = owner.token_id AND hide.collection_id = owner.collection_id)"

    println(sumQuery)
    try{
        var sum: Int? = null
        if ((account==null && collection_id==null) || (limit == null && page_number != null)) {
            throw Exception() // 예외 발생
        }
        if (connection != null) {
            val dbQueryExector = DBQueryExector(connection)
            val getNFT: ResultSet? = dbQueryExector.executeQuery(strQuery)
            val getSum: ResultSet? = dbQueryExector.executeQuery(sumQuery)
            if (getNFT != null) {
                try {
                    while (getNFT.next()) {
                        val objRes = JSONObject()

                        val network = getNFT.getString("network")
                        val collection_id = getNFT.getString("collection_id")
                        val collection_name = getNFT.getString("collection_name")
                        val collection_symbol = getNFT.getString("collection_symbol")
                        val collection_creator = getNFT.getString("creator")
                        val deployment_date = getNFT.getInt("deployment_date")
                        val total_supply = getNFT.getString("total_supply")
                        val nft_type = getNFT.getString("nft_type")
                        val minted_time = getNFT.getInt("minted_time")
                        val block_number = getNFT.getInt("block_number")
                        val owner_account = getNFT.getString("owner_account")
                        val token_id = getNFT.getString("token_id")
                        val balance = getNFT.getString("balance")
                        val token_uri = getNFT.getString("token_uri")
                        val nft_name = getNFT.getString("nft_name")
                        val description = getNFT.getString("description")
                        val image_url = getNFT.getString("image_url")
                        val external_url = getNFT.getString("external_url")
                        val attribute = getNFT.getString("attribute")
                        val token_info = getNFT.getString("token_info")

//                        val replace_attributes = JSONArray(attribute ?: "[]")
//                        val replace_metadata = JSONObject(token_info ?: "{}")

                        objRes.put("network", network)
                        objRes.put("collection_id", collection_id ?: JSONObject.NULL)
                        objRes.put("collection_name", collection_name ?: JSONObject.NULL)
                        objRes.put("collection_symbol", collection_symbol ?: JSONObject.NULL)
                        objRes.put("collection_creator", collection_creator ?: JSONObject.NULL)
                        objRes.put("collection_timestamp", deployment_date ?: JSONObject.NULL)
                        objRes.put("collection_total_supply", total_supply ?: JSONObject.NULL)
                        objRes.put("nft_type", nft_type ?: JSONObject.NULL)
                        objRes.put("minted_timestamp", minted_time ?: JSONObject.NULL)
                        objRes.put("block_number", block_number ?: JSONObject.NULL)
                        objRes.put("owner", owner_account ?: JSONObject.NULL)
                        objRes.put("token_id", token_id ?: JSONObject.NULL)
                        objRes.put("token_balance", balance ?: JSONObject.NULL)
                        objRes.put("token_uri", token_uri ?: JSONObject.NULL)
                        objRes.put("name", nft_name ?: JSONObject.NULL)
                        objRes.put("description", description ?: JSONObject.NULL)
                        objRes.put("image", image_url ?: JSONObject.NULL)
                        objRes.put("external_url", external_url ?: JSONObject.NULL)
                        objRes.put("attributes", attribute ?: JSONObject.NULL)
                        objRes.put("metadata", token_info ?: JSONObject.NULL)

                        nftArray.put(objRes)
                    }
                }
                catch (ex: SQLException){
                    ex.printStackTrace()
                }
                finally {
                    getNFT.close()
                }
            }
            //sum 출력
            if(getSum != null){
                try {
                    while (getSum.next()) {
                        sum = getSum.getInt("sum")
                        //nftData.put("sum", sum)
                    }
                }
                catch (ex: SQLException){
                    ex.printStackTrace()
                }
                finally {
                    getSum.close()
                }
            }
        }
        dbConnector.disconnect()

        val limit: Int? = limit
        val page_count: Int? = if (sum != null && limit != null) {
            Math.ceil(sum.toDouble() / limit.toDouble()).toInt()
        } else {
            0
        }

        nftData.put("result", "OK")
        nftData.put("sum", sum)
        val sort = sort ?: "desc" // 기본값을 "default_value"로 지정하거나 원하는 대체값을 사용하세요.
        nftData.put("sort", sort)
        nftData.put("page_count", page_count)
        nftData.put("value", nftArray)
    }
    catch (e: Exception) {
        nftData.put("result", "FAIL")
        nftData.put("error", e.message)
    }
}
suspend fun getNFTsByWalletArray(
    network: Array<String>,
    account: Array<String> ?= null,
    collection_id: String ?= null,
    sort: String ?= null,
    limit: Int ?= null,
    page_number: Int ?= null
): JSONObject = withContext(Dispatchers.IO) {

    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val nftArray = JSONArray() // { ..., value : [ nftArray ]}
    val nftData = JSONObject() // { "result": OK, "sum": 1, "sort": "desc", "page_count": 1, "value" : nftArray }

    val net = network.joinToString("','", "'", "'")
    val acc = account?.joinToString("','", "'", "'")

//    var offset = limit?.let { (page_number?.minus(1))?.times(it) }
    var offset = if (page_number != null && limit != null) {
        (page_number - 1) * limit
    } else {
        0 // 또는 적절한 기본값 설정
    }
    if(page_number==null || page_number==0 || page_number==1){
        offset = 0
    }
    var strQuery =
        "SELECT" +
                " owner.network AS network," +
                " collection.collection_id AS collection_id," +
                " collection.collection_name AS collection_name," +
                " collection.collection_symbol AS collection_symbol," +
                " collection.creator AS creator," +
                " collection.deployment_date AS deployment_date," +
                " collection.total_supply AS total_supply," +
                " token.nft_type AS nft_type," +
                " token.minted_time AS minted_time," +
                " token.block_number AS block_number," +
                " owner.owner_account AS owner_account," +
                " token.token_id AS token_id," +
                " owner.balance AS balance," +
                " token.token_uri AS token_uri," +
                " token.nft_name AS nft_name," +
                " token.description AS description," +
                " token.image_url AS image_url," +
                " token.external_url AS external_url," +
                " token.attribute AS attribute," +
                " token.token_info AS token_info" +
                " FROM " +
                "nft_owner_table AS owner" +
                " JOIN " +
                "nft_token_table AS token " +
                "ON " +
                "owner.collection_id = token.collection_id " +
                "AND " +
                "owner.token_id = token.token_id " +
                "AND " +
                "owner.network = token.network" +
                " JOIN " +
                "nft_collection_table AS collection " +
                "ON " +
                "token.collection_id = collection.collection_id " +
                "AND " +
                "token.network = collection.network " +
                "WHERE " +
                "owner.network IN (${net}) " +
                "AND " +
                "owner.balance != '0'"
    if (account != null) {
        strQuery += " AND owner.owner_account IN ($acc)"
    }
    if (collection_id != null) {
        strQuery += " AND owner.collection_id = '$collection_id'"
    }
    strQuery += " AND NOT EXISTS ( SELECT 1 FROM nft_hide_table AS hide WHERE hide.network = owner.network AND hide.account = owner.owner_account AND hide.token_id = owner.token_id AND hide.collection_id = owner.collection_id)"
    strQuery += " ORDER BY token.block_number"
    if (sort == "asc") {
        strQuery += " asc"
    } else {
        strQuery += " desc"
    }
    strQuery += ", CAST(token.token_id AS SIGNED) desc"
    if (limit != null) {
        strQuery += " LIMIT $limit OFFSET $offset"
    }
    println(strQuery)

    var sumQuery =
        "SELECT " +
                " count(*) AS sum" +
                " FROM" +
                " nft_owner_table AS owner" +
                " JOIN" +
                " nft_token_table AS token" +
                " ON" +
                " owner.collection_id = token.collection_id" +
                " AND" +
                " owner.token_id = token.token_id" +
                " AND" +
                " owner.network = token.network" +
                " JOIN" +
                " nft_collection_table AS collection" +
                " ON" +
                " token.collection_id = collection.collection_id" +
                " AND" +
                " token.network = collection.network" +
                " WHERE" +
                " owner.network IN ($net)" +
                " AND" +
                " owner.balance != '0'"
    if (account != null) {
        sumQuery += " AND owner.owner_account IN ($acc) "
    }
    if (collection_id != null) {
        sumQuery += " AND owner.collection_id = '$collection_id' "
    }
    sumQuery += " AND NOT EXISTS ( SELECT 1 FROM nft_hide_table AS hide WHERE hide.network = owner.network AND hide.account = owner.owner_account AND hide.token_id = owner.token_id AND hide.collection_id = owner.collection_id)"

    println(sumQuery)
    try{
        var sum: Int? = null
        if ((account==null && collection_id==null) || (limit == null && page_number != null)) {
            throw Exception() // 예외 발생
        }
        if (connection != null) {
            val dbQueryExector = DBQueryExector(connection)
            val getNFT: ResultSet? = dbQueryExector.executeQuery(strQuery)
            val getSum: ResultSet? = dbQueryExector.executeQuery(sumQuery)
            if (getNFT != null) {
                try {
                    while (getNFT.next()) {
                        val objRes = JSONObject()

                        val network = getNFT.getString("network")
                        val collection_id = getNFT.getString("collection_id")
                        val collection_name = getNFT.getString("collection_name")
                        val collection_symbol = getNFT.getString("collection_symbol")
                        val collection_creator = getNFT.getString("creator")
                        val deployment_date = getNFT.getInt("deployment_date")
                        val total_supply = getNFT.getString("total_supply")
                        val nft_type = getNFT.getString("nft_type")
                        val minted_time = getNFT.getInt("minted_time")
                        val block_number = getNFT.getInt("block_number")
                        val owner_account = getNFT.getString("owner_account")
                        val token_id = getNFT.getString("token_id")
                        val balance = getNFT.getString("balance")
                        val token_uri = getNFT.getString("token_uri")
                        val nft_name = getNFT.getString("nft_name")
                        val description = getNFT.getString("description")
                        val image_url = getNFT.getString("image_url")
                        val external_url = getNFT.getString("external_url")
                        val attribute = getNFT.getString("attribute")
                        val token_info = getNFT.getString("token_info")

//                        val replace_attributes = JSONArray(attribute ?: "[]")
//                        val replace_metadata = JSONObject(token_info ?: "{}")

                        objRes.put("network", network)
                        objRes.put("collection_id", collection_id ?: JSONObject.NULL)
                        objRes.put("collection_name", collection_name ?: JSONObject.NULL)
                        objRes.put("collection_symbol", collection_symbol ?: JSONObject.NULL)
                        objRes.put("collection_creator", collection_creator ?: JSONObject.NULL)
                        objRes.put("collection_timestamp", deployment_date ?: JSONObject.NULL)
                        objRes.put("collection_total_supply", total_supply ?: JSONObject.NULL)
                        objRes.put("nft_type", nft_type ?: JSONObject.NULL)
                        objRes.put("minted_timestamp", minted_time ?: JSONObject.NULL)
                        objRes.put("block_number", block_number ?: JSONObject.NULL)
                        objRes.put("owner", owner_account ?: JSONObject.NULL)
                        objRes.put("token_id", token_id ?: JSONObject.NULL)
                        objRes.put("token_balance", balance ?: JSONObject.NULL)
                        objRes.put("token_uri", token_uri ?: JSONObject.NULL)
                        objRes.put("name", nft_name ?: JSONObject.NULL)
                        objRes.put("description", description ?: JSONObject.NULL)
                        objRes.put("image", image_url ?: JSONObject.NULL)
                        objRes.put("external_url", external_url ?: JSONObject.NULL)
                        objRes.put("attributes", attribute ?: JSONObject.NULL)
                        objRes.put("metadata", token_info ?: JSONObject.NULL)

                        nftArray.put(objRes)
                    }
                }
                catch (ex: SQLException){
                    ex.printStackTrace()
                }
                finally {
                    getNFT.close()
                }
            }
            //sum 출력
            if(getSum != null){
                try {
                    while (getSum.next()) {
                        sum = getSum.getInt("sum")
                        //nftData.put("sum", sum)
                    }
                }
                catch (ex: SQLException){
                    ex.printStackTrace()
                }
                finally {
                    getSum.close()
                }
            }
        }
        dbConnector.disconnect()

        val limit: Int? = limit
        val page_count: Int? = if (sum != null && limit != null) {
            Math.ceil(sum.toDouble() / limit.toDouble()).toInt()
        } else {
            0
        }

        nftData.put("result", "OK")
        nftData.put("sum", sum)
        val sort = sort ?: "desc" // 기본값을 "default_value"로 지정하거나 원하는 대체값을 사용하세요.
        nftData.put("sort", sort)
        nftData.put("page_count", page_count)
        nftData.put("value", nftArray)
    }
    catch (e: Exception) {
        nftData.put("result", "FAIL")
        nftData.put("error", e.message)
    }
}

//getNFTTransaction
@SuppressLint("SuspiciousIndentation")
suspend fun getNFTsTransferHistory(
    network: String,
    collection_id: String ?= null,
    token_id: String ?= null,
    type: String ?= null,
    sort: String ?= null,
    limit: Int ?= null,
    page_number: Int ?= null
) : JSONObject = withContext(Dispatchers.IO){

    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val transactionArray = JSONArray()
    val transferData = JSONObject()

    var offset = if (page_number != null && limit != null) {
        (page_number - 1) * limit
    } else {
        0 // 또는 적절한 기본값 설정
    }
    if(page_number==null || page_number==0 || page_number==1){
        offset = 0
    }

    var transferQuery =
        " SELECT" +
                " transfer.network AS network," +
                " sales.buyer_account AS buyer_account," +
                " transfer.`from` AS from_address," +
                " transfer.`to` AS to_address," +
                " transfer.collection_id AS collection_id," +
                " transfer.block_number AS block_number," +
                " transfer.`timestamp` AS timestamp," +
                " transfer.transaction_hash AS transaction_hash," +
                " transfer.log_id AS log_id," +
                " transfer.token_id AS token_id," +
                " transfer.amount AS amount," +
                " sales.currency AS currency," +
                " sales.currency_symbol AS currency_symbol," +
                " sales.decimals AS decimals," +
                " sales.price AS price," +
                " sales.market AS market," +
                " sales.sales_info AS sales_info," +
                " CASE" +
                " WHEN" +
                " sales.sales_info IS NOT NULL THEN 'sales'" +
                " ELSE" +
                " 'transfer'" +
                " END AS" +
                " transaction_type" +
                " FROM" +
                " nft_transfer_table AS transfer" +
                " LEFT OUTER JOIN" +
                " nft_sales_table AS sales" +
                " ON" +
                " transfer.transaction_hash = sales.transaction_hash" +
                " AND" +
                " transfer.network = sales.network" +
                " LEFT JOIN" +
                " nft_transaction_type_table AS type" +
                " ON" +
                " transfer.transaction_hash = type.transaction_hash" +
                " AND" +
                " transfer.network = type.network" +
                " WHERE" +
                " transfer.network = '${network}'"
    if(token_id != null){
        transferQuery += " AND transfer.token_id = '${token_id}' "
    }
    if(collection_id != null){
        transferQuery += " AND transfer.collection_id= '${collection_id}' "
    }
    if(type=="transfer"){
        transferQuery += "AND type.transaction_type = 'transfer' ORDER BY transfer.block_number"
    }
    else if(type=="sales"){
        transferQuery += "AND type.transaction_type = 'sales' ORDER BY transfer.block_number"
    }
    else{
        transferQuery += " ORDER BY transfer.block_number"
    }
    if(sort == "asc"){
        transferQuery += " asc"
    } else {
        transferQuery += " desc"
    }
    transferQuery += ", CAST(transfer.token_id AS SIGNED) desc"
    if (limit != null) {
        transferQuery += " LIMIT ${limit} OFFSET ${offset}"
    }
    println(transferQuery)

    var sumQuery =
        "SELECT" +
                " count(*) AS sum" +
                " FROM " +
                " nft_transfer_table AS transfer" +
                " LEFT JOIN " +
                " nft_sales_table AS sales" +
                " ON " +
                " transfer.transaction_hash = sales.transaction_hash" +
                " AND" +
                " transfer.network = sales.network" +
                " LEFT JOIN " +
                " nft_transaction_type_table AS type" +
                " ON " +
                " transfer.transaction_hash = type.transaction_hash" +
                " AND" +
                " transfer.network = type.network" +
                " WHERE " +
                " transfer.network = '$network'"
    if (token_id != null) {
        sumQuery += " AND transfer.token_id = '$token_id' "
    }
    if (collection_id != null) {
        sumQuery += " AND transfer.collection_id = '$collection_id' "
    }
    if(type != null){
        sumQuery += " AND type.transaction_type = '${type}'"
    }
    println(sumQuery)
    try {
        var sum: Int? = null
        if ((token_id == null && collection_id == null) || (limit == null && page_number != null)) {
            throw Exception() // 예외 발생
        }
        if (connection != null) {
            val dbQueryExector = DBQueryExector(connection)
            val getTransaction1: ResultSet? = dbQueryExector.executeQuery(transferQuery)
            val getSum: ResultSet? = dbQueryExector.executeQuery(sumQuery)
            if (getTransaction1 != null) {
                try {
                    while (getTransaction1.next()) {
                        val jsonData = JSONObject()
                        // Select data = network, from, to, collection_id, block_number, timestamp, transaction_hash, log_id, token_id, amount, transaction_type
                        val network = getTransaction1.getString("network")
                        val buyer_account = getTransaction1.getString("buyer_account")
                        val from_address = getTransaction1.getString("from_address")
                        val to_address = getTransaction1.getString("to_address")
                        val collection_id = getTransaction1.getString("collection_id")
                        val block_number = getTransaction1.getInt("block_number")
                        val timestamp = getTransaction1.getInt("timestamp")
                        val transaction_hash = getTransaction1.getString("transaction_hash")
                        val log_id = getTransaction1.getString("log_id")
                        val token_id = getTransaction1.getString("token_id")
                        val amount = getTransaction1.getString("amount")
                        val currency = getTransaction1.getString("currency")
                        val currency_symbol = getTransaction1.getString("currency_symbol")
                        val decimals = getTransaction1.getInt("decimals")
                        val price = getTransaction1.getString("price")
                        val market = getTransaction1.getString("market")
                        val sales_info = getTransaction1.getString("sales_info")
                        val transaction_type = getTransaction1.getString("transaction_type")

                        val replace_sales_info = JSONArray(sales_info ?: "[]")

                        jsonData.put("network", network)
                        jsonData.put("buyer", buyer_account)
                        jsonData.put("from", from_address)
                        jsonData.put("to", to_address)
                        jsonData.put("collection_id", collection_id)
                        jsonData.put("block_number", block_number)
                        jsonData.put("timestamp", timestamp)
                        jsonData.put("transaction_hash", transaction_hash)
                        jsonData.put("log_id", log_id)
                        jsonData.put("token_id", token_id)
                        jsonData.put("amount", amount)
                        jsonData.put("currency", currency)
                        jsonData.put("currency_symbol", currency_symbol)
                        jsonData.put("decimals", decimals)
                        jsonData.put("price", price)
                        jsonData.put("market", market)
                        jsonData.put("sales_info", replace_sales_info)
                        jsonData.put("type", transaction_type)

                        transactionArray.put(jsonData)
                    }
                }
                catch (ex: SQLException) {
                    ex.printStackTrace()
                } finally {
                    getTransaction1.close()
                }
            }
            if(getSum != null){
                try {
                    while (getSum.next()) {
                        sum = getSum.getInt("sum")
                    }
                }
                catch (ex: SQLException){
                    ex.printStackTrace()
                }
                finally {
                    getSum.close()
                }
            }
        }
        dbConnector.disconnect()

        val limit: Int? = limit
        val page_count: Int? = if (sum != null && limit != null) {
            Math.ceil(sum.toDouble() / limit.toDouble()).toInt()
        } else {
            0
        }

        transferData.put("result", "OK")
        transferData.put("sum", sum)
        val sort = sort ?: "desc" // 기본값을 "default_value"로 지정하거나 원하는 대체값을 사용하세요.
        transferData.put("sort", sort)
        transferData.put("page_count", page_count)
        transferData.put("value", transactionArray)
    }
    catch (e: Exception){
        transferData.put("result", "FAIL")
        transferData.put("error", e.message)
    }
}
//숨김테이블 조회
suspend fun getNFTsHide(
    network: Array<String>,
    account: Array<String>,
    sort: String ?= null,
    limit: Int ?= null,
    page_number: Int ?= null
): JSONObject = withContext(Dispatchers.IO){
    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val hideData = JSONObject()
    val hideArray = JSONArray()

    val net = network.joinToString("','", "'", "'")
    val acc = account.joinToString("','", "'", "'")

    var offset = if (page_number != null && limit != null) {
        (page_number - 1) * limit
    } else {
        0 // 또는 적절한 기본값 설정
    }
    if(page_number==null || page_number==0 || page_number==1){
        offset = 0
    }

    var hideQuery =
        "SELECT " +
                "hide.network AS network, " +
                "hide.account AS account, " +
                "hide.collection_id AS collection_id, " +
                "hide.token_id AS token_id, " +
                "hide.image_url AS image_url, " +
                "hide.nft_name AS nft_name " +
                "FROM " +
                "nft_hide_table AS hide " +
                "WHERE " +
                "hide.network IN (${net}) " +
                "AND " +
                "hide.account IN (${acc}) "
    hideQuery += " ORDER BY idx"
    if (sort == "asc") {
        hideQuery += " asc"
    } else {
        hideQuery += " desc"
    }

    if (limit != null) {
        hideQuery += " LIMIT $limit OFFSET $offset"
    }

    var sumQuery =
        "SELECT" +
                " count(*) AS sum" +
                " FROM " +
                " nft_hide_table" +
                " WHERE " +
                "network IN (${net}) " +
                "AND " +
                "account IN (${acc}) "
    println(sumQuery)
    print(hideQuery)

    try {
        var sum: Int? = null

        if (connection != null) {
            val dbQueryExector = DBQueryExector(connection)
            val getTransaction1: ResultSet? = dbQueryExector.executeQuery(hideQuery)
            val getSum: ResultSet? = dbQueryExector.executeQuery(sumQuery)
            if (getTransaction1 != null) {
                try {
                    while (getTransaction1.next()) {
                        val jsonData = JSONObject()

                        val network = getTransaction1.getString("network")
                        val account = getTransaction1.getString("account")
                        val collection_id = getTransaction1.getString("collection_id")
                        val token_id = getTransaction1.getString("token_id")
                        val image_url = getTransaction1.getString("image_url")
                        val nft_name = getTransaction1.getString("nft_name")

                        jsonData.put("network", network)
                        jsonData.put("account", account)
                        jsonData.put("collection_id", collection_id)
                        jsonData.put("token_id", token_id)
                        jsonData.put("image", image_url)
                        jsonData.put("name", nft_name)

                        hideArray.put(jsonData)
                    }
                }
                catch (ex: SQLException) {
                    ex.printStackTrace()
                } finally {
                    getTransaction1.close()
                }
            }
            if(getSum != null){
                try {
                    while (getSum.next()) {
                        sum = getSum.getInt("sum")
                    }
                }
                catch (ex: SQLException){
                    ex.printStackTrace()
                }
                finally {
                    getSum.close()
                }
            }
        }
        dbConnector.disconnect()

        val limit: Int? = limit
        val page_count: Int? = if (sum != null && limit != null) {
            Math.ceil(sum.toDouble() / limit.toDouble()).toInt()
        } else {
            0
        }

        hideData.put("result", "OK")
        hideData.put("sum", sum)
        val sort = sort ?: "desc" // 기본값을 "default_value"로 지정하거나 원하는 대체값을 사용하세요.
        hideData.put("sort", sort)
        hideData.put("page_count", page_count)
        hideData.put("value", hideArray)
    }
    catch (e: Exception){
        hideData.put("result", "FAIL")
        hideData.put("error", e.message)
    }
}

suspend fun sendNFT721TransactionAsync(
    network: String,
    from: String,
    to: String,
    token_id: String,
    collection_id: String
): JSONObject = withContext(Dispatchers.IO){
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }

        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val function = Function(
            "safeTransferFrom",
            listOf(Address(from), Address(to), Uint256(BigInteger(token_id))),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "transferERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    token_id
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "transferERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    token_id
                ),
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun sendNFT1155TransactionAsync(
    network: String,
    from: String,
    to: String,
    token_id: String,
    collection_id: String,
    amount: String,
): JSONObject = withContext(Dispatchers.IO) {
    val jsonData = JSONObject()
    networkSettings(network)
    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val ethGasPrice = web3j.ethGasPrice().sendAsync().get()

        // Ensure amount is a valid number
        if (BigInteger(amount) <= BigInteger.ZERO) {
            jsonData.put("result", "FAIL")
            jsonData.put("error", "insufficient funds")
            return@withContext jsonData
        }

        val function = Function(
            "safeTransferFrom",
            listOf(
                Address(from), Address(to), Uint256(BigInteger(token_id)),
                Uint256(BigInteger(amount)), DynamicBytes(byteArrayOf(0))
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val gasPrice = ethGasPrice.gasPrice

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "transferERC1155",
                    collection_id,
                    from,
                    to,
                    amount,
                    token_id
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(network, "transferERC1155",collection_id, from, to, amount, token_id),
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun sendNFT721BatchTransactionAsync(
    network: String,
    from: String,
    to: String,
    token_id: Array<String>,
    collection_id: String
): JSONObject = withContext(Dispatchers.IO) {
    val jsonData = JSONObject()
    networkSettings(network)
    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val ethGasPrice = web3j.ethGasPrice().sendAsync().get()

        val batchTokenId = token_id.map { Uint256(BigInteger(it)) }

        val function = Function(
            "safeBatchTransferFrom",
            listOf(
                Address(from), Address(to), DynamicArray(batchTokenId)
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val gasPrice = ethGasPrice.gasPrice

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "batchTransferERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null,
                    null,
                    null,
                    token_id
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "batchTransferERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null,
                    null,
                    null,
                    token_id
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun sendNFT1155BatchTransactionAsync(
    network: String,
    from: String,
    to: String,
    token_id: Array<String>,
    collection_id: String,
    amount: Array<String>,
): JSONObject = withContext(Dispatchers.IO) {

    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val ethGasPrice = web3j.ethGasPrice().sendAsync().get()

        // Ensure amount is a valid number
        for(a in amount) {
            if (BigInteger(a) <= BigInteger.ZERO) {
                jsonData.put("result", "FAIL")
                jsonData.put("error", "insufficient funds")
                return@withContext jsonData
            }
        }
        val batchTokenId = token_id.map { Uint256(BigInteger(it)) }
        val batchAmount = amount.map { Uint256(BigInteger(it)) }

        val function = Function(
            "safeBatchTransferFrom",
            listOf(
                Address(from), Address(to), DynamicArray(batchTokenId), DynamicArray(batchAmount), DynamicBytes(byteArrayOf(0))
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val gasPrice = ethGasPrice.gasPrice

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "batchTransferERC1155",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null,
                    null,
                    null,
                    token_id,
                    amount
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "batchTransferERC1155",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null,
                    null,
                    null,
                    token_id,
                    amount
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun deployErc721Async(
    network: String,
    from: String,
    name: String,
    symbol: String,
    token_base_uri: String,
    owner: String,
    uri_type: String
): JSONObject = withContext(Dispatchers.IO) {
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val function = Function(
            "deployedERC721",
            listOf(
                Utf8String(name),
                Utf8String(symbol),
                Utf8String(token_base_uri),
                Uint8(BigInteger(uri_type)),
                Address(owner)
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "deployERC721",
                    null,
                    from,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    name, symbol, owner, token_base_uri, uri_type
                ), // Add 20% to the gas limit
                erc721DeployContractAddress,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "deployERC721",
                    null,
                    from,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    name, symbol, owner, token_base_uri, uri_type
                ),
                erc721DeployContractAddress,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result", "OK")
        jsonData.put("transactionHash", txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun deployErc1155Async(
    network: String,
    from: String,
    name: String,
    symbol: String,
    token_base_uri: String,
    owner: String,
    uri_type: String
): JSONObject = withContext(Dispatchers.IO) {
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val function = Function(
            "deployedERC1155",
            listOf(
                Utf8String(name),
                Utf8String(symbol),
                Utf8String(token_base_uri),
                Uint8(BigInteger(uri_type)),
                Address(owner)
            ),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "deployERC1155",
                    null,
                    from,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    name, symbol, owner, token_base_uri, uri_type
                ), // Add 20% to the gas limit
                erc1155DeployContractAddress,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "deployERC1155",
                    null,
                    from,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    name, symbol, owner, token_base_uri, uri_type
                ),
                erc1155DeployContractAddress,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result", "OK")
        jsonData.put("transactionHash", txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}


suspend fun mintErc721Async(
    network: String,
    from: String,
    to: String,
    token_uri: String,
    token_id: String,
    collection_id: String
): JSONObject = withContext(Dispatchers.IO){
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val function = Function(
            "mint",
            listOf(Address(to), Uint256(BigInteger(token_id)), Utf8String(token_uri)),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "mintERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    token_uri
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "mintERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    token_uri
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun mintErc1155Async(
    network: String,
    from: String,
    to: String,
    token_uri: String,
    token_id: String,
    collection_id: String,
    amount: String
): JSONObject = withContext(Dispatchers.IO){
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val function = Function(
            "mint",
            listOf(Address(to), Uint256(BigInteger(token_id)), Uint256(BigInteger(amount)), Utf8String(token_uri), DynamicBytes(byteArrayOf(0))),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "mintERC1155",
                    collection_id,
                    from,
                    to,
                    amount,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    token_uri
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "mintERC1155",
                    collection_id,
                    from,
                    to,
                    amount,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    token_uri
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun batchMintErc721Async(
    network: String,
    from: String,
    to: String,
    token_uri: Array<String>,
    token_id: Array<String>,
    collection_id: String
): JSONObject = withContext(Dispatchers.IO){
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val batchTokenId = token_id.map { Uint256(BigInteger(it)) }
        val batchTokenURI = token_uri.map { Utf8String(it) }

        val function = Function(
            "mintBatch",
            listOf(Address(to), DynamicArray(batchTokenId), DynamicArray(batchTokenURI)),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "batchMintERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null, null, null, token_id, null, null, null, null, null, null,
                    null, token_uri
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "batchMintERC721",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null, null, null, token_id, null, null, null, null, null, null,
                    null, token_uri
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}
suspend fun batchMintErc1155Async(
    network: String,
    from: String,
    to: String,
    token_uri: Array<String>,
    token_id: Array<String>,
    collection_id: String,
    amount: Array<String>
): JSONObject = withContext(Dispatchers.IO){
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(from)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val batchTokenId = token_id.map { Uint256(BigInteger(it)) }
        val batchAmount = amount.map { Uint256(BigInteger(it)) }
        val batchTokenURI = token_uri.map { Utf8String(it) }

        val function = Function(
            "mintBatch",
            listOf(Address(to), DynamicArray(batchTokenId), DynamicArray(batchAmount), DynamicArray(batchTokenURI), DynamicBytes(byteArrayOf(0))),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "batchMintERC1155",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null, null, null, token_id, amount, null, null, null, null, null,
                    null, token_uri
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "batchMintERC1155",
                    collection_id,
                    from,
                    to,
                    null,
                    null,
                    null, null, null, token_id, amount, null, null, null, null, null,
                    null, token_uri
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun burnErc721Async(
    network: String,
    owner: String,
    token_id: String,
    collection_id: String
): JSONObject = withContext(Dispatchers.IO){
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(owner)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val function = Function(
            "burn",
            listOf(Uint256(BigInteger(token_id))),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(owner, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "burnERC721",
                    collection_id,
                    owner,
                    null,
                    null,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "burnERC721",
                    collection_id,
                    owner,
                    null,
                    null,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun burnErc1155Async(
    network: String,
    owner: String,
    token_id: String,
    collection_id: String,
    amount: String
): JSONObject = withContext(Dispatchers.IO){
    networkSettings(network)
    val jsonData = JSONObject()

    try {
        val getAddressInfo = getAccountInfoAsync(owner)
        val privateKey = runCatching {
            getAddressInfo.getJSONArray("value")
                .getJSONObject(0)
                .getString("private")
        }.getOrElse {
            // handle error here
            println("Error while fetching the private key: ${it.message}")
            null
        }
        val web3j = Web3j.build(HttpService(rpcUrl))
        val credentials =
            Credentials.create(privateKey)

        val function = Function(
            "burn",
            listOf(Address(owner), Uint256(BigInteger(token_id)), Uint256(BigInteger(amount))),
            emptyList()
        )
        val encodedFunction = FunctionEncoder.encode(function)

        val nonce = web3j.ethGetTransactionCount(owner, DefaultBlockParameterName.PENDING)
            .sendAsync()
            .get()
            .transactionCount

        val chainId = web3j.ethChainId().sendAsync().get().chainId.toLong()
        val tx = if (network == "bnb" || network == "bnbTest") {
            RawTransaction.createTransaction(
                nonce,
                getEstimateGasAsync(network, "baseFee"), // Add 20% to the gas price
                getEstimateGasAsync(
                    network,
                    "burnERC1155",
                    collection_id,
                    owner,
                    null,
                    amount,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null
                ), // Add 20% to the gas limit
                collection_id,
                encodedFunction
            )
        } else {
            RawTransaction.createTransaction(
                chainId,
                nonce,
                getEstimateGasAsync(
                    network,
                    "burnERC1155",
                    collection_id,
                    owner,
                    null,
                    amount,
                    token_id,
                    null, null, null, null, null, null, null, null, null, null,
                    null, null
                ), // Add 20% to the gas limit
                collection_id,
                BigInteger.ZERO,
                encodedFunction,
                //0.1gwei
                BigInteger("50000000000"),
                getEstimateGasAsync(network, "baseFee")
            )
        }

        val signedMessage = TransactionEncoder.signMessage(tx, credentials)
        val signedTx = Numeric.toHexString(signedMessage)

        val txHash = web3j.ethSendRawTransaction(signedTx).sendAsync().get().transactionHash
        jsonData.put("result","OK")
        jsonData.put("transactionHash",txHash)
    } catch (e: Exception) {
        jsonData.put("result", "FAIL")
        jsonData.put("error", e.message)
    }
}

suspend fun verifyNFT(
    network: String,
    tokenId: String,
    contractAddress: String,
    apiKey: String
): JSONObject = withContext(Dispatchers.IO) {
    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val result = JSONObject()
    result.put("ContractVerify", false)
    result.put("TokenURIAvailable", false)
    result.put("TokenURIResponseOnTime", false)
    result.put("TokenURIDecentralized", false)
    result.put("MetadataStandard", false)
    result.put("MetadataImageAvailable", false)
    result.put("TokenURIisHTTPS", false)
    result.put("ImageURIisHTTPS", false)

    var nftType: String? = null
    var tokenURI: String? = null
    var tokenInfo: String? = null
    var imageURL: String? = null
    var query =
        "SELECT nft_type, token_uri, token_info, image_url FROM " +
                "nft_token_table " +
                "WHERE " +
                "network = '${network}' " +
                "AND " +
                "collection_id = '${contractAddress}' " +
                "AND " +
                "token_id = '${tokenId}' "
    try {
        if (connection != null) {
            val dbQueryExector = DBQueryExector(connection)
            val getTransaction1: ResultSet? = dbQueryExector.executeQuery(query)
            if (getTransaction1 != null) {
                try {
                    while (getTransaction1.next()) {
                        nftType = getTransaction1.getString("nft_type")
                        tokenURI = getTransaction1.getString("token_uri")
                        tokenInfo = getTransaction1.getString("token_info")
                        imageURL = getTransaction1.getString("image_url")
                    }
                }
                catch (ex: SQLException) {
                    ex.printStackTrace()
                } finally {
                    getTransaction1.close()
                }
            }
        }
        val hostUrl: String
        when (network) {
            "ethereum" -> {
                hostUrl = "https://api.etherscan.com/api?module=contract&action=getabi&address=$contractAddress&apikey=$apiKey"
            }
            "cypress" -> {
                hostUrl = ""
            }
            "polygon" -> {
                hostUrl = "https://api.polygonscan.com/api?module=contract&action=getabi&address=$contractAddress&apikey=$apiKey"
            }
            "bnb" -> {
                hostUrl = "https://api.bscscan.com/api?module=contract&action=getabi&address=$contractAddress&apikey=$apiKey"
            }
            else -> {
                hostUrl = ""
            }
        }

        if(network == "cypress") {
            result.put("ContractVerify", false)
            result.put("ContractStandard", false)
        } else {
            val url = URL(hostUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseData = InputStreamReader(connection.inputStream).readText()

                val jsonObject = JSONObject(responseData)
                val apiResult = jsonObject.optString("result")

                if (apiResult == "Contract source code not verified") {
                    result.put("ContractVerify", false)
                    result.put("ContractStandard", false)
                } else {
                    val apiResultJson = JSONArray(apiResult)
                    result.put("ContractVerify", true)

                    val bytecodeFunctions = mutableListOf<JSONObject>()

                    for (i in 0 until apiResultJson.length()) {
                        val jsonObject = apiResultJson.getJSONObject(i)
                        if (jsonObject.getString("type") == "function") {
                            bytecodeFunctions.add(jsonObject)
                        }
                    }

                    val contractStandard = JSONObject()

                    if (nftType == "erc721") {
                        val balanceOf =
                            bytecodeFunctions.any { it.getString("name") == "balanceOf" }
                        val ownerOf = bytecodeFunctions.any { it.getString("name") == "ownerOf" }
                        val transferFrom =
                            bytecodeFunctions.any { it.getString("name") == "transferFrom" }
                        val approve = bytecodeFunctions.any { it.getString("name") == "approve" }
                        val setApprovalForAll =
                            bytecodeFunctions.any { it.getString("name") == "setApprovalForAll" }
                        val getApproved =
                            bytecodeFunctions.any { it.getString("name") == "getApproved" }
                        val isApprovedForAll =
                            bytecodeFunctions.any { it.getString("name") == "isApprovedForAll" }

                        var safeTransferFromWith_data = false
                        var safeTransferFromWithout_data = false

                        for (jsonObject in bytecodeFunctions) {
                            val name = jsonObject.getString("name")
                            if (name == "safeTransferFrom") {
                                val inputs = jsonObject.getJSONArray("inputs")
                                var hasData = false
                                for (i in 0 until inputs.length()) {
                                    val input = inputs.getJSONObject(i)
                                    if (input.getString("name") == "_data") {
                                        hasData = true
                                        break
                                    }
                                }
                                if (hasData) {
                                    safeTransferFromWith_data = true
                                } else {
                                    safeTransferFromWithout_data = true
                                }
                            }
                        }

                        contractStandard.put("balanceof", balanceOf)
                        contractStandard.put("ownerOf", ownerOf)
                        contractStandard.put("transferFrom", transferFrom)
                        contractStandard.put("approve", approve)
                        contractStandard.put("setApprovalForAll", setApprovalForAll)
                        contractStandard.put("getApproved", getApproved)
                        contractStandard.put("isApprovedForAll", isApprovedForAll)
                        contractStandard.put("safeTransferFromWith_data", safeTransferFromWith_data)
                        contractStandard.put(
                            "safeTransferFromWithout_data",
                            safeTransferFromWithout_data
                        )
                    } else if (nftType == "erc1155") {
                        val balanceOf =
                            bytecodeFunctions.any { it.getString("name") == "balanceOf" }
                        val balanceOfBatch =
                            bytecodeFunctions.any { it.getString("name") == "balanceOfBatch" }
                        val setApprovalForAll =
                            bytecodeFunctions.any { it.getString("name") == "setApprovalForAll" }
                        val isApprovedForAll =
                            bytecodeFunctions.any { it.getString("name") == "isApprovedForAll" }
                        val safeTransferFrom =
                            bytecodeFunctions.any { it.getString("name") == "safeTransferFrom" }
                        val safeBatchTransferFrom =
                            bytecodeFunctions.any { it.getString("name") == "safeBatchTransferFrom" }

                        contractStandard.put("balanceof", balanceOf)
                        contractStandard.put("balanceOfBatch", balanceOfBatch)
                        contractStandard.put("setApprovalForAll", setApprovalForAll)
                        contractStandard.put("isApprovedForAll", isApprovedForAll)
                        contractStandard.put("safeTransferFrom", safeTransferFrom)
                        contractStandard.put("safeBatchTransferFrom", safeBatchTransferFrom)
                    }

                    result.put("ContractStandard", contractStandard)
                }

            } else {
                println("Invalid API response. Response code: $responseCode")
                result
            }
        }

        networkSettings(network!!)
        val web3 = Web3j.build(HttpService(rpcUrl))
        var parameter = Numeric.hexStringToByteArray("0x80ac58cd")
        if(nftType == "erc721") {
            parameter = Numeric.hexStringToByteArray("0x80ac58cd")

        } else if(nftType == "erc1155"){
            parameter = Numeric.hexStringToByteArray("0xd9b67a26")
        }
        val supportsInterface = Function(
            "supportsInterface",
            listOf(Bytes4(parameter)),
            listOf(object : TypeReference<Bool>() {})
        )
        val encodedsupportsInterface = FunctionEncoder.encode(supportsInterface)
        val supportsInterfaceResponse = web3.ethCall(
            Transaction.createEthCallTransaction(null, contractAddress, encodedsupportsInterface),
            DefaultBlockParameterName.LATEST
        ).send()
        val supportsInterfaceOutput =
            FunctionReturnDecoder.decode(supportsInterfaceResponse.result, supportsInterface.outputParameters)
        val isSupported = supportsInterfaceOutput[0].value as Boolean
        if (isSupported) {
            result.put("supportsInterface", true)
        } else {
            result.put("supportsInterface", false)
        }

        if(!tokenURI.isNullOrEmpty()) {
            var uriHttp = ""
            if (tokenURI.startsWith("ipfs://ipfs/")) {
                uriHttp = "https://ipfs.io/ipfs/${tokenURI.substring(12)}"
            } else if (tokenURI.startsWith("ipfs://")) {
                uriHttp = "https://ipfs.io/ipfs/${tokenURI.substring(7)}"
            } else if (tokenURI.startsWith("ar://")) {
                uriHttp = "https://arweave.net/${tokenURI.substring(5)}"
            } else {
                uriHttp = tokenURI
            }

            if (uriHttp.contains("ipfs") || uriHttp.contains("arweave")) {
                result.put("TokenURIDecentralized", true)
            }

            val startTime = Date()
            val url = URL(uriHttp)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val endTime = Date()

                val elapsedTime = (endTime.time - startTime.time) / 1000

                result.put("TokenURIAvailable", true)

                if (elapsedTime < 2) {
                    result.put("TokenURIResponseOnTime", true)
                }
            } else {
                println("Invalid API response. Response code: $responseCode")
            }

            if (url.protocol == "https") {
                result.put("TokenURIisHTTPS", true)
            }
        }

        if (tokenInfo != null) {
            try {
                val json = JSONObject(tokenInfo)

                var trueCount = 0

                if (json.has("name")) {
                    trueCount += 1
                }

                if (json.has("image")) {
                    trueCount += 1

                    if (imageURL!!.startsWith("ipfs://ipfs/")) {
                        imageURL = "https://ipfs.io/ipfs/${imageURL.substring(12)}"
                    } else if (imageURL.startsWith("ipfs://")) {
                        imageURL = "https://ipfs.io/ipfs/${imageURL.substring(7)}"
                    } else if (imageURL.startsWith("ar://")) {
                        imageURL = "https://arweave.net/${imageURL.substring(5)}"
                    }

                    try {
                        val imageConnection = URL(imageURL!!).openConnection() as HttpURLConnection
                        imageConnection.requestMethod = "HEAD"

                        if (imageConnection.responseCode == HttpURLConnection.HTTP_OK) {
                            result.put("MetadataImageAvailable", true)
                        }
                    } catch (e: IOException) {
                        println("Image fetch error: ${e.message}")
                    }

                    if (imageURL!!.startsWith("https://")) {
                        result.put("ImageURIisHTTPS", true)
                    }
                }

                if (json.has("description")) {
                    trueCount += 1
                }

                if (json.has("attributes")) {
                    trueCount += 1
                }

                if (trueCount == 4) {
                    result.put("MetadataStandard", true)
                }
            } catch (e: Exception) {
                println("JSON parsing error: ${e.message}")
            }
        }

        result
    } catch (e: Exception) {
        println(e)
        result
    }
}

suspend fun chkNFTHolder(
    network: String,
    account: String,
    collection_id: String,
    token_id: String
): JSONObject = withContext(Dispatchers.IO){
    val dbConnector = DBConnector()
    dbConnector.connect()
    val connection = dbConnector.getConnection()
    val result = JSONObject()
    var query =
        "SELECT " +
                "network, " +
                "collection_id, " +
                "token_id, " +
                "nft_type " +
                "FROM " +
                "nft_token_table " +
                "WHERE " +
                "network = '${network}' " +
                "AND " +
                "collection_id = '${collection_id}' " +
                "AND " +
                "token_id = '${token_id}' "

    try {
        var network: String? = null
        var collection_id: String? = null
        var token_id: String? = null
        var nft_type: String? = null
        if (connection != null) {
            val dbQueryExector = DBQueryExector(connection)
            val getTransaction1: ResultSet? = dbQueryExector.executeQuery(query)
            if (getTransaction1 != null) {
                try {
                    while (getTransaction1.next()) {
                        network = getTransaction1.getString("network")
                        collection_id = getTransaction1.getString("collection_id")
                        token_id = getTransaction1.getString("token_id")
                        nft_type = getTransaction1.getString("nft_type")
                    }
                }
                catch (ex: SQLException) {
                    ex.printStackTrace()
                } finally {
                    getTransaction1.close()
                }
            }
        }
        dbConnector.disconnect()

        if(nft_type == null) {
            result.put("result", "FAIL")
            result.put("error", "DB info is null")
            return@withContext result
        }

        networkSettings(network!!)
        val web3 = Web3j.build(HttpService(rpcUrl))
        if(nft_type == "erc721") {
            val ownerFunction = Function(
                "ownerOf",
                listOf(Uint256(BigInteger(token_id))),
                listOf(object : TypeReference<Address>() {})
            )
            val encodedOwnerFunction = FunctionEncoder.encode(ownerFunction)
            val ownerResponse = web3.ethCall(
                Transaction.createEthCallTransaction(null, collection_id, encodedOwnerFunction),
                DefaultBlockParameterName.LATEST
            ).send()
            val ownerOutput =
                FunctionReturnDecoder.decode(ownerResponse.result, ownerFunction.outputParameters)
            val owner = ownerOutput[0].value
            if (owner == account) {
                result.put("result", "OK")
            } else {
                result.put("result", "FAIL")
                result.put("error", "NOT OWNER")
            }
        } else {
            val balanceOfFunction = Function(
                "balanceOf",
                listOf(Address(account), Uint256(BigInteger(token_id))),
                listOf(object : TypeReference<Uint256>() {})
            )
            val encodedbalanceOfFunction = FunctionEncoder.encode(balanceOfFunction)
            val balanceOfResponse = web3.ethCall(
                Transaction.createEthCallTransaction(null, collection_id, encodedbalanceOfFunction),
                DefaultBlockParameterName.LATEST
            ).send()
            val balanceOfOutput =
                FunctionReturnDecoder.decode(balanceOfResponse.result, balanceOfFunction.outputParameters)
            val balance = (balanceOfOutput[0].value as BigInteger).toInt()
            if (balance >= 1) {
                result.put("result", "OK")
            } else {
                result.put("result", "FAIL")
                result.put("error", "NOT OWNER")
            }
        }
    }
    catch (e: Exception){
        result.put("result", "FAIL")
        result.put("error", e.message)
    }
}
