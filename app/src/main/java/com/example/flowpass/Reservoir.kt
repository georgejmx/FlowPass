package com.example.flowpass

import com.example.flowpass.database.Entry
import com.example.flowpass.database.Silt
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * A class to manage validation of user generated hashes and encryption/decryption
 * of user data. Generates a key from the provided hash that is then used across
 * the app as a filter between the database and UI
 */
class Reservoir(dir: File) {

    // The key which is used for AES encryption
    private lateinit var key: SecretKeySpec
    // Root app directory; for storing keys locally
    private val directory: File = dir

    private var isOpen: Boolean = false

    // At registration, create the private AES key and validation parameters
    fun create(hash: ByteArray, purity: String) {
        key = generateKey(hash)
        File(directory, "vZx45c").writeText(purity, Charset.defaultCharset())
        File(directory, "B6e4Vj").writeBytes(encryptCell(purity))
    }

    // At login attempt, check that the user provided the correct password
    fun open(hash: ByteArray): Boolean {
        key = generateKey(hash)
        if (validatePurity()) { isOpen = true }
        return isOpen
    }

    // Converts the desired database [Entry] into [Sludge], encrypting
    // the entire tuple
    fun buryData(entry: Entry): Silt {
        if (isOpen) {
            val cells: Array<ByteArray> = arrayOf(
                encryptCell(entry.name),
                encryptCell(entry.key1),
                encryptCell(entry.key2),
                encryptCell(entry.key3),
                encryptCell(entry.password)
            )
            return Silt(
                entry._id, cells[0], cells[1], cells[2], cells[3], cells[4]
            )
        } else {
            throw ReservoirClosedException("The Water purity has not been validated!!")
        }
    }

    // Converts encrypted data [Sludge] into the original user [Entry]
    fun releaseData(sludge: Silt): Entry {
        if (isOpen) {
            val cells: Array<String> = arrayOf(
                decryptCell(sludge.field1),
                decryptCell(sludge.field2),
                decryptCell(sludge.field3),
                decryptCell(sludge.field4),
                decryptCell(sludge.field5)
            )
            return Entry(
                sludge._id, cells[0], cells[1], cells[2], cells[3], cells[4]
            )
        } else {
            throw ReservoirClosedException("The water purity has not been validated!!!")
        }
    }

    // Closes the reservoir; prevents further interactions with user data
    fun close() { isOpen = false }

    // Deletes all trace of keys seed and keys
    fun clearData() {
        if (isOpen) {
            val path1 = File(directory, "B6e4Vj").absolutePath
            val path2 = File(directory, "vZx45c").absolutePath
            Files.delete(Paths.get(path1))
            Files.delete(Paths.get(path2))
        } else {
            throw ReservoirClosedException("The water purity has not been validated!!!")
        }
    }

    // Takes the password hash, processes with SHA, then generates key
    private fun generateKey(hash: ByteArray): SecretKeySpec {
        val shaInstance = MessageDigest.getInstance("SHA-1")
        var shaDigest = shaInstance.digest(hash)
        shaDigest = Arrays.copyOf(shaDigest, 16)
        return SecretKeySpec(shaDigest, "AES")
    }

    // Checks whether user inputted password is valid or not
    private fun validatePurity(): Boolean {
        return try {
            val predictedPurity = decryptCell(File(directory, "B6e4Vj").readBytes())
            val purity = File(directory, "vZx45c").readText(Charset.defaultCharset())
            predictedPurity == purity
        } catch (err: GeneralSecurityException) {
            // Log this
            false
        }
    }

    // Encrypt string data to ByteArray using AES
    private fun encryptCell(msg: String): ByteArray {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(msg.toByteArray(charset("UTF-8")))
    }

    // Decrypt encrypted ByteArray to the original string using AES
    private fun decryptCell(hiddenMsg: ByteArray): String {
        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, key)
        return String(cipher.doFinal(hiddenMsg))
    }

}