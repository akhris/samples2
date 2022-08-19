package test

import domain.Condition

object Conditions {
    val condition1 = Condition(name = "+25")
    val condition2 = Condition(name = "+125")
    val condition3 = Condition(name = "-60")
    val list = listOf(condition1, condition2, condition3)
}