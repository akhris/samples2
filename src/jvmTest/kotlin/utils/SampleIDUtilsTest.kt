package utils

import org.junit.jupiter.api.Test


internal class SampleIDUtilsTest {

    val rawIDs = "1,10, 30, 50-60ab 44,    15 5-6b"


    @Test
    fun parseSampleIDs() {
        val parsedIDs = SampleIDUtils.parseSampleIDs(rawIDs)
        log("parsedIDs: $parsedIDs")
    }
}