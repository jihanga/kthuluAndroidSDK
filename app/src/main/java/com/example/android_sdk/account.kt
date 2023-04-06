import org.web3j.crypto.*
import org.web3j.utils.Numeric

fun getRandomString() : String {
    val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    return (1..10)
        .map { charset.random() }
        .joinToString("")
}

fun main() {
    val mainNet = listOf("ethereum", "klaytn", "polygon", "binace")
    val account = createAccount(mainNet)
    println("New Account:\n$account\n")
    val mnemonic = account.first()["mnemonic"] as String
    val getaccount = restoreAccount("ethereum", mnemonic)
    println("Get Account:\n$getaccount\n")
}

private fun createAccount(mainNet: List<String>): List<Map<String, Any>> {
    val password = getRandomString()
    val bip39Wallet = WalletUtils.generateBip39Wallet(password, null)
    val masterKeyPair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(bip39Wallet.mnemonic, ""))
    val purpose = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT))
    val coinType = Bip32ECKeyPair.deriveKeyPair(purpose, intArrayOf(60 or Bip32ECKeyPair.HARDENED_BIT))
    val account = Bip32ECKeyPair.deriveKeyPair(coinType, intArrayOf(0 or Bip32ECKeyPair.HARDENED_BIT))
    val change = Bip32ECKeyPair.deriveKeyPair(account, intArrayOf(0))
    val keyPair = Bip32ECKeyPair.deriveKeyPair(change, intArrayOf(0))
    val credentials = Credentials.create(keyPair.privateKey.toString(16))
    return mainNet.map { network ->
        mapOf(
            "mainNet" to network,
            "address" to credentials.address,
            "private" to "0x${Numeric.toHexStringNoPrefix(keyPair.privateKey)}",
            "mnemonic" to bip39Wallet.mnemonic
        )
    }
}


private fun restoreAccount(mainNet: String, mnemonic: String): Map<String, Any> {
    val masterKeyPair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(mnemonic, ""))
    val purpose = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, intArrayOf(44 or Bip32ECKeyPair.HARDENED_BIT))
    val coinType = Bip32ECKeyPair.deriveKeyPair(purpose, intArrayOf(60 or Bip32ECKeyPair.HARDENED_BIT))
    val account = Bip32ECKeyPair.deriveKeyPair(coinType, intArrayOf(0 or Bip32ECKeyPair.HARDENED_BIT))
    val change = Bip32ECKeyPair.deriveKeyPair(account, intArrayOf(0))
    val keyPair = Bip32ECKeyPair.deriveKeyPair(change, intArrayOf(0))
    val credentials = Credentials.create(keyPair.privateKey.toString(16))
    return mapOf(
        "mainNet" to mainNet,
        "address" to credentials.address,
        "private" to "0x${Numeric.toHexStringNoPrefix(keyPair.privateKey)}",
        "mnemonic" to mnemonic
    )
}
