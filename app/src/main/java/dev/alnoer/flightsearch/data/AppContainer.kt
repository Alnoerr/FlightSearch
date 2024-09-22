package dev.alnoer.flightsearch.data

import android.content.Context
import dev.alnoer.flightsearch.data.repository.FlightSearchRepository
import dev.alnoer.flightsearch.data.repository.OfflineFlightSearchRepository

interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val flightSearchRepository: FlightSearchRepository by lazy {
        val database = FlightSearchDatabase.getDatabase(context)
        OfflineFlightSearchRepository(
            airportDao = database.airportDao(),
            favoriteDao = database.favoriteDao()
        )
    }
}