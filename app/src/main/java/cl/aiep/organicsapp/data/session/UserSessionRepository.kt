package cl.aiep.organicsapp.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import cl.aiep.organicsapp.core.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserSessionRepository(
    private val dataStore: DataStore<Preferences>
) : SessionRepository {

    override val profile: Flow<UserProfile?> = dataStore.data.map { preferences ->
        val name = preferences[KEY_NAME].orEmpty()
        val email = preferences[KEY_EMAIL].orEmpty()
        val hasSession = preferences[KEY_HAS_SESSION] ?: false

        if (!hasSession) {
            null
        } else {
            UserProfile(
                name = name.ifBlank { "Invitado" },
                email = email,
                receiveOffers = preferences[KEY_RECEIVE_OFFERS] ?: false,
                isGuest = preferences[KEY_IS_GUEST] ?: false
            )
        }
    }

    override suspend fun save(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[KEY_HAS_SESSION] = true
            preferences[KEY_NAME] = profile.name
            preferences[KEY_EMAIL] = profile.email
            preferences[KEY_RECEIVE_OFFERS] = profile.receiveOffers
            preferences[KEY_IS_GUEST] = profile.isGuest
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_HAS_SESSION)
            preferences.remove(KEY_NAME)
            preferences.remove(KEY_EMAIL)
            preferences.remove(KEY_RECEIVE_OFFERS)
            preferences.remove(KEY_IS_GUEST)
        }
    }

    private companion object {
        val KEY_HAS_SESSION = booleanPreferencesKey("has_session")
        val KEY_NAME = stringPreferencesKey("user_name")
        val KEY_EMAIL = stringPreferencesKey("user_email")
        val KEY_RECEIVE_OFFERS = booleanPreferencesKey("receive_offers")
        val KEY_IS_GUEST = booleanPreferencesKey("is_guest")
    }
}
