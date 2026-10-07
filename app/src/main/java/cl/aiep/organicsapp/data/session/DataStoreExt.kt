package cl.aiep.organicsapp.data.session

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.userSessionDataStore by preferencesDataStore(name = "organics_session")
