package test

import domain.Norm

object Norms {
    val norm1 = Norm(parameter = Parameters.par1, condition = Conditions.condition1)
    val norm2 = Norm(parameter = Parameters.par1, condition = Conditions.condition2)
    val norm3 = Norm(parameter = Parameters.par1, condition = Conditions.condition3)
    val norm4 = Norm(parameter = Parameters.par2, condition = Conditions.condition1)
    val norm5 = Norm(parameter = Parameters.par2, condition = Conditions.condition2)
    val norm6 = Norm(parameter = Parameters.par2, condition = Conditions.condition3)
    val norm7 = Norm(parameter = Parameters.par3, condition = Conditions.condition1)
    val norm8 = Norm(parameter = Parameters.par3, condition = Conditions.condition2)
    val norm9 = Norm(parameter = Parameters.par3, condition = Conditions.condition3)

    val norms = listOf(
        norm1,
        norm2,
        norm3,
        norm4,
        norm5,
        norm6,
        norm7,
        norm8,
        norm9,
    )
}