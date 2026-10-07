package cl.aiep.organicsapp.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailValidatorTest {
    @Test
    fun validEmail_returnsTrue() {
        assertTrue(EmailValidator.isValid("cliente@example.com"))
    }

    @Test
    fun invalidEmail_returnsFalse() {
        assertFalse(EmailValidator.isValid("correo-invalido"))
    }
}
