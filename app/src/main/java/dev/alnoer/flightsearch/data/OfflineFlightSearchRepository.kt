package dev.alnoer.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFlightSearchRepository(
    private val flightSearchDao: FlightSearchDao
) : FlightSearchRepository {
    override fun getFlightsStream(searchQuery: String): Flow<List<Airport>> =
        flightSearchDao.getFlights(searchQuery)

    override fun getFlightSuggestionsStream(searchQuery: String): Flow<List<Airport>> =
        flightSearchDao.getFlightSuggestions(searchQuery)

    override fun getFavoritesStream(): Flow<List<Favorite>> = flightSearchDao.getFavorites()
}