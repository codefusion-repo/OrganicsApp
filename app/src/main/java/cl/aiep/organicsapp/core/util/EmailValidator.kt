package cl.aiep.organicsapp.core.util

object EmailValidator {
    private val pattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    fun isValid(email: String): Boolean = pattern.matches(email.trim())
}
