package domain.valueobjects

sealed class Factor(val factor: Int, val prefix: String, val name: String) {
    object NoFactor : Factor(1, "_", "no factor")
    object Femto : Factor(-15, "f", "femto")
    object Pico : Factor(-12, "p", "pico")
    object Nano : Factor(-9, "n", "nano")
    object Micro : Factor(-6, "u", "micro")
    object Milli : Factor(-3, "m", "milli")
    object Centi : Factor(-2, "c", "centi")
    object Kilo : Factor(3, "k", "kilo")
    object Mega : Factor(6, "M", "mega")
    object Giga : Factor(9, "G", "giga")
    object Tera : Factor(12, "T", "tera")
    object Peta : Factor(15, "P", "peta")

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
