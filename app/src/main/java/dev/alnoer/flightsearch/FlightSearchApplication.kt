package dev.alnoer.flightsearch

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dev.alnoer.flightsearch.data.AppContainer
import dev.alnoer.flightsearch.data.DefaultAppContainer
import dev.alnoer.flightsearch.data.UserPreferencesRepository

private const val SEARCH_PREFERENCES = "search_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SEARCH_PREFERENCES
)

class FlightSearchApplication : Application() {
    lateinit var container: AppContainer
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        container = DefaultAppContainer(this)
    }
}