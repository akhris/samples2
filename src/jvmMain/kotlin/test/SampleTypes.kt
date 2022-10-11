package test

import domain.SampleType

object SampleTypes {
    val type1 = SampleType(name = "test_ic1", description = "half-bridge driver")
    val type2 = SampleType(name = "test_ic2", description = "full-bridge driver")
    val type3 = SampleType(name = "test_mosfet1", description = "power mosfet")
    val list = listOf(type1, type2, type3)
}