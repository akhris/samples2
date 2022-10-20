package utils

import kotlin.math.*

object SampleIDUtils {
    /**
     * function to parse id's given in the form of ranges or list in string:
     * "1-10, 54, 64, 70-80a 543"
     */
    fun parseSampleIDs(rawIDs: String): List<String> {
//        log("going to parse ids from $rawIDs")
        val rangesRegex = "[0-9]+-[0-9]+[a-zA-Z]*".toRegex()
        val suffixesRegex = "[a-zA-Z]+".toRegex()
        val numericRegex = "[0-9]+".toRegex()

        val rangesFound = rangesRegex.findAll(rawIDs).map { it.value }.toList()
        val nonRangesIDs = numericRegex.findAll(rawIDs.replace(rangesRegex, "")).map { it.value }.toList()


//        log("ranges found: $rangesFound")
//        log("nonRangesIDs: $nonRangesIDs")

        val idsFromRange =
            rangesFound
                .flatMap { rangeIDs ->
                    val suffix = suffixesRegex.find(rangeIDs)?.value ?: ""
                    val range = numericRegex.findAll(rangeIDs).mapNotNull { it.value.toIntOrNull() }
                    val from = range.firstOrNull() ?: return@flatMap listOf()
                    val to = range.lastOrNull() ?: return@flatMap listOf()
                    if (from == to) return@flatMap listOf()
                    (min(from, to)..max(from, to)).map {
                        "${it}${suffix}"
                    }
                }

        return nonRangesIDs.sorted().plus(idsFromRange).distinct()
    }
}