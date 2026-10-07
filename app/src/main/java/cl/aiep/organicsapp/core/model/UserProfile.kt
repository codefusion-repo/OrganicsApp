package cl.aiep.organicsapp.core.model

data class UserProfile(
    val name: String,
    val email: String,
    val receiveOffers: Boolean,
    val isGuest: Boolean = false
)
