package domain.valueobjects

sealed class Factor(val factor: Int, val prefix: String, val name: String) {
    object NoFactor : Factor(1, "_", "без приставки")
    object Femto : Factor(-15, "ф", "фемто")
    object Pico : Factor(-12, "п", "пико")
    object Nano : Factor(-9, "н", "нано")
    object Micro : Factor(-6, "мк", "микро")
    object Milli : Factor(-3, "м", "милли")
    object Centi : Factor(-2, "с", "санти")
    object Kilo : Factor(3, "к", "кило")
    object Mega : Factor(6, "М", "мега")
    object Giga : Factor(9, "Г", "гига")
    object Tera : Factor(12, "Т", "тера")
    object Peta : Factor(15, "П", "пета")

    override fun toString(): String = name

    companion object {
        fun parse(factor: Int?): Factor {
            return factors.find { it.factor == factor } ?: NoFactor
        }
    }
}

val Factor.scientificString: String
    get() {
        return when (factor) {
            1 -> "1"
            else -> "1.0E$factor"
        }
    }

val factors: List<Factor> = listOf(
    Factor.Femto,
    Factor.Pico,
    Factor.Nano,
    Factor.Micro,
    Factor.Milli,
    Factor.Centi,
    Factor.NoFactor,
    Factor.Kilo,
    Factor.Mega,
    Factor.Giga,
    Factor.Tera,
    Factor.Peta
)
