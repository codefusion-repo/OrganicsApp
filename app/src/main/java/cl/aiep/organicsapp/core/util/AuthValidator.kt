package cl.aiep.organicsapp.core.util

object AuthValidator {
    fun isValidName(name: String): Boolean = name.trim().length >= 3
    fun isValidPassword(password: String): Boolean = password.length >= 8
}
