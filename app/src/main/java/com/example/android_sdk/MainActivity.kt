package com.example.android_sdk

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

// Application에서 Context를 가져올 수 있도록 구현
class MyContext : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
    }
}

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setConfiguration(
            getString(R.string.kthulu_database_url),
            getString(R.string.kthulu_database_username),
            getString(R.string.kthulu_database_password),
            getString(R.string.kthulu_database_driver_class_name)
        )

//        var network = arrayOf("ethereum", "cypress", "polygon", "bnb")
//        val ownerArray = arrayOf("0x0eae45485F2D14FDEB3dAa1143E5170752D5EAe8", "0xf084a7C7052a053cd6950ce83c934443aAE8813c")
//        var accountArray = arrayOf("0x0eae45485F2D14FDEB3dAa1143E5170752D5EAe8", "0xf084a7C7052a053cd6950ce83c934443aAE8813c", "0xa2f8cD45cd7EA14bcE6e87f177cf9DF928a089A5")
//        var account = "0x13418f99467D20A7970a36fe4F2fF6Ec494A3A12"
//        var collection_id = "0x22d5f9B75c524Fec1D6619787e582644CD4D7422"
//        var sort = "desc"
//        var limit = 10
//        var page_number = 1
//        val accountsAddtess = "0x1C3c32DFB8cBe1E144d5e79eB41392e535405C40"
        // Using coroutines to avoid blocking the UI thread
        launch {
            withContext(Dispatchers.IO) {
//                account()
//                transaction()
//                var getNFTsByWallet= getNFTsByWallet(
//                    network = network,
//                    account = account,
//                    collection_id = collection_id,
//                    sort = sort,
//                    limit = limit,
//                    page_number = page_number
//                )
//                println("getNFTsByWallet ===== " + getNFTsByWallet)
//                var getNFTsByWalletArray= getNFTsByWalletArray(
//                    network = network,
//                    account = accountArray
//                    collection_id = collection_id,
//                    sort = sort,
//                    limit = limit,
//                    page_number = page_number
//                )
//                println("getNFTsByWalletArray ===== " + getNFTsByWalletArray)
//                var getNFTsTransferHistory =
//                    getNFTsTransferHistory(
//                        network = "polygon",
//                        collection_id = "0xba6666B118f8303F990f3519DF07e160227cCE87",
//                        token_id = "7",
////                        type = "transfer",
////                        sort = "desc",
////                        limit = 10,
////                        page_number = 1
//                    )
//                println("getNFTsTransferHistory ==== " + getNFTsTransferHistory)
//                  var gas = getEstimateGasAsync("ethereum", "baseFee")
//                  println(gas)
//                    var gasBydeploy = getEstimateGasAsync("goerli", "deployERC721", null, "0x0eae45485F2D14FDEB3dAa1143E5170752D5EAe8", null, null, null, null, null, null, null, null, "asd", "asd", "0x0eae45485F2D14FDEB3dAa1143E5170752D5EAe8", "asd", "1")
//                    println(gasBydeploy)
//                  var sendNFT = sendNFT1155BatchTransactionAsync("goerli", "0x772A779d280d19C2bd582B4F3840De703249CA39", "0x54fbF887EdB01983DD373E79a0f37413B4565De3", arrayOf("182","189"), "0x5a644acd663d7e4d07eeabe43df0f985670f8f9a", arrayOf("1","1"))
//                  println(sendNFT)
//                var setNFTsHide = setNFTsHide("polygon", "8xrhpmtw0w", "0xCfEEc46f729F6A7274ffC1eD4972276E96Cc7Ae5", "0xC16643b44E427A7C5Caa0C682Ec672F12B592e83", "15558")
//                println("setNFTsHide === "+ setNFTsHide)
//                var deleteNFTsHide = deleteNFTsHide("polygon", "8xrhpmtw0w", "0xCfEEc46f729F6A7274ffC1eD4972276E96Cc7Ae5", "0xC16643b44E427A7C5Caa0C682Ec672F12B592e83", "15558")
//                println("deleteNFTsHide === "+ deleteNFTsHide)
//                var getNFTsHide = getNFTsHide(network = network, account = accountArray)
//                println("getNFTsHide === " + getNFTsHide)
//                var getMintableAddress = getMintableAddress(owner = ownerArray)
//                println("getMintableAddress === " + getMintableAddress)
//                var sendNFT = sendNFT721TransactionAsync("polygon", "0xeC4eC414c1f6a0759e5d184E17dB45cCd87E09FD", "0x174115aB58d633Ec0f356E8b2FD3e4F70c542ea7", "106884999638613214598024973249647500981424504920603967513583525150680917475329", "0x35f8aee672cde8e5fd09c93d2bfe4ff5a9cf0756")
//                  println(sendNFT)
//                var mint1155 = mintErc1155Async("polygon", "0xec4ec414c1f6a0759e5d184e17db45ccd87e09fd", "0xec4ec414c1f6a0759e5d184e17db45ccd87e09fd", "2020.json", "2020", "0x556D27664D0126ff1126761dA5c6A4c0E061C05B", "100")
//                println(mint1155)
//                var sendNFT = sendNFT1155TransactionAsync("polygon", "0xeC4eC414c1f6a0759e5d184E17dB45cCd87E09FD", "0xf084a7C7052a053cd6950ce83c934443aAE8813c", "2020", "0x556D27664D0126ff1126761dA5c6A4c0E061C05B", "1")
//                  println(sendNFT)
//                var signme = signMessage()
//                println(signme);
//                var a = chkNFTHolder("polygon", "0x867a270c8e18c3173a259340732ea16abff76908", "0x2bf59f7908B6588F6B9Abd11284719775330bc21", "140078")
//                println(a)
//                 var verify = verifyNFT("cypress", "41100060085", "0xa9A95C5feF43830D5d67156a2582A2E793aCb465", "")
//                println(verify)

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel() // Cancel all coroutines when the activity is destroyed
    }
}


