package cl.aiep.organicsapp.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthValidatorTest {
    @Test
    fun password_requiresAtLeastEightCharacters() {
        assertTrue(AuthValidator.isValidPassword("Demo1234!"))
        assertFalse(AuthValidator.isValidPassword("1234567"))
    }

    @Test
    fun name_requiresAtLeastThreeCharacters() {
        assertTrue(AuthValidator.isValidName("Ana"))
        assertFalse(AuthValidator.isValidName("Jo"))
    }
}
