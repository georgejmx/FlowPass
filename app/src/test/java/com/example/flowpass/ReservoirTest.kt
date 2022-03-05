package com.example.flowpass

import com.example.flowpass.database.Entry
import com.example.flowpass.database.Silt
import java.io.File
import java.nio.charset.Charset
import java.security.MessageDigest
import org.junit.Test
import kotlin.test.*

/**
 * Checks that [Reservoir] behaves as expected; that the encryption process
 * is watertight
 */
class ReservoirTest {

    // Getting the root testing directory where dummy files will be written
    private val rootDir = System.getProperty("user.dir")

    @Test
    // Check that core password validation works
    fun reservoirCreationValidationWorks() {
        // check creation of reservoir
        val rvr = Reservoir(File(rootDir))
        rvr.create(passToHash("pass1TEST\$*777"), generateRandomPurity())
        assertTrue(File("vZx45c").exists())
        assertTrue(File("B6e4Vj").exists())

        // check correct validation passes
        assertTrue(rvr.open(passToHash("pass1TEST$*777")))
    }

    @Test
    // Check that spoof validation does not work
    fun reservoirCreationValidationFails() {
        // check creation of reservoir
        val rvr = Reservoir(File(rootDir))
        rvr.create(passToHash("pass1"), "b2")
        assertTrue(File("vZx45c").exists())
        assertTrue(File("B6e4Vj").exists())

        // check spoof validation fails
        assertTrue(!rvr.open(passToHash("pass2")))
        assertTrue(!rvr.open(passToHash("")))
        assertTrue(!rvr.open(passToHash("pass1TEST$*767")))
    }

    @Test
    // Check that password entries can be stored properly
    fun entryStoreAndRetrievalWorks() {
        // initialise reservoir
        val rvr = Reservoir(File(rootDir))
        rvr.create(passToHash("pass1"), "123456789012345678901234567890")
        val res = rvr.open(passToHash("pass1"))
        assertTrue(res)

        // testing entry
        val testEntry = Entry(
            1,
            "test",
            "",
            " ",
            "creative?!%(+",
            "MARK67!?"
        )
        val buriedTestEntry: Silt = rvr.buryData(testEntry)
        assertNotNull(buriedTestEntry.field2)
        assertNotEquals(" ", bytesToString(buriedTestEntry.field3))
        val releasedTestEntry: Entry = rvr.releaseData(buriedTestEntry)
        assertEquals(testEntry, releasedTestEntry)
    }

    @Test
    // Check that retrieval does not work on closed reservoir or with
    // tampered key
    fun entryStoreAndRetrievalBlocks() {
        // initialise reservoir
        val rvr = Reservoir(File(rootDir))
        rvr.create(passToHash("!pass1"), generateRandomPurity())
        val res = rvr.open(passToHash("pass2"))
        assertTrue(!res)

        // testing ReservoirClosedException
        val testEntry = Entry(
            9999,
            "test",
            "null",
            " ",
            "logical?!%(+",
            "ELON67!?"
        )
        assertFailsWith<ReservoirClosedException> { rvr.buryData(testEntry) }

        // testing that tampering with the file breaks things
        File("vZx45c").writeText("FACES66", Charset.defaultCharset())
        assertTrue(!rvr.open(passToHash("!pass1")))
    }

    // Hash any input String into a ByteArray
    private fun passToHash(pass: String): ByteArray {
        val passInBytes = pass.toByteArray(Charsets.UTF_8)
        val digester = MessageDigest.getInstance("MD5")
        return digester.digest(passInBytes)
    }

    // Generates a random string that has 26873856 possible combinations, for
    // use in encryption
    private fun generateRandomPurity(): String {
        val metals = arrayOf(
            "Calcium", "Magnesium", "Lithium", "Zinc", "Strontium", "Iron"
        )
        val getNum = { (1..12).shuffled().first().toString() }
        val getMetalInt = { (0..5).shuffled().first() }
        val resultBuild = StringBuilder()
        for (i in 0..4) {
            resultBuild.append(metals[getMetalInt()])
            resultBuild.append(getNum())
        }
        return resultBuild.toString()
    }

    // Convert any ByteArray to a String
    private fun bytesToString(bts: ByteArray): String {
        val sb = StringBuilder()
        for (b in bts) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

}