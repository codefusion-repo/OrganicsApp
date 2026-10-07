package cl.aiep.organicsapp.data.session

import cl.aiep.organicsapp.core.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    val profile: Flow<UserProfile?>
    suspend fun save(profile: UserProfile)
    suspend fun clear()
}
