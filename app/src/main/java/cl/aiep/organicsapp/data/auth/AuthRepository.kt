package cl.aiep.organicsapp.data.auth

import cl.aiep.organicsapp.core.model.UserProfile

sealed interface AuthResult {
    data class Success(val profile: UserProfile) : AuthResult
    data class Error(val message: String) : AuthResult
}

interface AuthRepository {
    suspend fun login(email: String, password: String, receiveOffers: Boolean): AuthResult
    suspend fun register(name: String, email: String, password: String, receiveOffers: Boolean): AuthResult
}
