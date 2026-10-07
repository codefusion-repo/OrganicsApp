package cl.aiep.organicsapp.data.auth

import cl.aiep.organicsapp.core.model.UserProfile
import cl.aiep.organicsapp.core.util.AuthValidator
import cl.aiep.organicsapp.core.util.EmailValidator

class LocalAuthRepository : AuthRepository {

    private val accounts = mutableMapOf(
        DEMO_EMAIL to LocalAccount(DEMO_NAME, DEMO_PASSWORD)
    )

    override suspend fun login(
        email: String,
        password: String,
        receiveOffers: Boolean
    ): AuthResult {
        val normalizedEmail = email.trim().lowercase()

        if (!EmailValidator.isValid(normalizedEmail)) {
            return AuthResult.Error("Ingresa un correo electrónico válido.")
        }

        val account = accounts[normalizedEmail]
        if (account == null || account.password != password) {
            return AuthResult.Error("Credenciales incorrectas. Puedes usar el usuario demo indicado en el formulario.")
        }

        return AuthResult.Success(
            UserProfile(
                name = account.name,
                email = normalizedEmail,
                receiveOffers = receiveOffers,
                isGuest = false
            )
        )
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String,
        receiveOffers: Boolean
    ): AuthResult {
        val normalizedName = name.trim()
        val normalizedEmail = email.trim().lowercase()

        if (!AuthValidator.isValidName(normalizedName)) {
            return AuthResult.Error("El nombre debe contener al menos 3 caracteres.")
        }
        if (!EmailValidator.isValid(normalizedEmail)) {
            return AuthResult.Error("Ingresa un correo electrónico válido.")
        }
        if (!AuthValidator.isValidPassword(password)) {
            return AuthResult.Error("La contraseña debe contener al menos 8 caracteres.")
        }
        if (accounts.containsKey(normalizedEmail)) {
            return AuthResult.Error("Ya existe una cuenta local con ese correo.")
        }

        accounts[normalizedEmail] = LocalAccount(normalizedName, password)

        return AuthResult.Success(
            UserProfile(
                name = normalizedName,
                email = normalizedEmail,
                receiveOffers = receiveOffers,
                isGuest = false
            )
        )
    }

    private data class LocalAccount(
        val name: String,
        val password: String
    )

    companion object {
        const val DEMO_EMAIL = "demo@organicsapp.cl"
        const val DEMO_PASSWORD = "Demo1234!"
        const val DEMO_NAME = "Cliente Demo"
    }
}
