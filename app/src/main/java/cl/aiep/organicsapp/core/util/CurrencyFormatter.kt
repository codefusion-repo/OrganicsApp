package cl.aiep.organicsapp.core.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    private val formatter: NumberFormat = NumberFormat.getCurrencyInstance(
        Locale.forLanguageTag("es-CL")
    ).apply {
        maximumFractionDigits = 0
        minimumFractionDigits = 0
    }

    fun format(value: Int): String = formatter.format(value)
}
