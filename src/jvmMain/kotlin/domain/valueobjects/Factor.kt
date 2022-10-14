package domain.valueobjects

import kotlinx.serialization.Serializable

@Serializable
sealed class Factor(val factor: Int, val prefix: String, val name: String) {
    @Serializable
    object NoFactor : Factor(1, "_", "без приставки")
    @Serializable
    object Femto : Factor(-15, "ф", "фемто")
    @Serializable
    object Pico : Factor(-12, "п", "пико")
    @Serializable
    object Nano : Factor(-9, "н", "нано")
    @Serializable
    object Micro : Factor(-6, "мк", "микро")
    @Serializable
    object Milli : Factor(-3, "м", "милли")
    @Serializable
    object Centi : Factor(-2, "с", "санти")
    @Serializable
    object Kilo : Factor(3, "к", "кило")
    @Serializable
    object Mega : Factor(6, "М", "мега")
    @Serializable
    object Giga : Factor(9, "Г", "гига")
    @Serializable
    object Tera : Factor(12, "Т", "тера")
    @Serializable
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
