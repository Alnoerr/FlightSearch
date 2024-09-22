package dev.alnoer.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dev.alnoer.flightsearch.data.repository.FlightSearchRepository
import dev.alnoer.flightsearch.data.repository.OfflineFlightSearchRepository
import dev.alnoer.flightsearch.data.repository.UserPreferencesRepository

private const val SEARCH_PREFERENCES = "search_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = SEARCH_PREFERENCES
)
interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val userPreferencesRepository: UserPreferencesRepository =
        UserPreferencesRepository(context.dataStore)

    override val flightSearchRepository: FlightSearchRepository by lazy {
        val database = FlightSearchDatabase.getDatabase(context)
        OfflineFlightSearchRepository(
            airportDao = database.airportDao(),
            favoriteDao = database.favoriteDao()
        )
    }
}