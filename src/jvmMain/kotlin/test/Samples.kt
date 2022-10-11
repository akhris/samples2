package test

import domain.Sample

object Samples {
    val sample1 = Sample(id = "1", type = SampleTypes.type1)
    val sample2 = Sample(id = "2", type = SampleTypes.type1)
    val sample3 = Sample(id = "3", type = SampleTypes.type1)
    val sample4 = Sample(id = "4", type = SampleTypes.type2)
    val sample5 = Sample(id = "5", type = SampleTypes.type2)

    val samples = listOf(sample1, sample2, sample3, sample4, sample5)
}