package test

import domain.OperationType

object OperationTypes {
    val op1 = OperationType(name = "отжиг")
    val op2 = OperationType(name = "проверка герметичности")
    val op3 = OperationType(name = "обрезка выводов")
    val op4 = OperationType(name = "герметизация")
    val list = listOf(op1, op2, op3, op4)
}