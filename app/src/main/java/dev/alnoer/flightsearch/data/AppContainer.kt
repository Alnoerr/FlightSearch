package dev.alnoer.flightsearch.data

import android.content.Context

interface AppContainer {
    val flightSearchRepository: FlightSearchRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val flightSearchRepository: FlightSearchRepository by lazy {
        OfflineFlightSearchRepository(FlightSearchDatabase.getDatabase(context).flightSearchDao())
    }
}