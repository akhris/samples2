package test

import domain.Parameter

object Parameters {
    val par1 = Parameter(sampleType = SampleTypes.type1, parameterID = "Icc1", description = "Supply current 1")
    val par2 = Parameter(sampleType = SampleTypes.type1, parameterID = "Icc2", description = "Supply current 2")
    val par3 = Parameter(sampleType = SampleTypes.type1, parameterID = "Icc3", description = "Supply current 3")
    val list = listOf(par1, par2, par3)
}