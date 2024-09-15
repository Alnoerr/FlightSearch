package dev.alnoer.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFlightSearchRepository(
    private val flightSearchDao: FlightSearchDao
) : FlightSearchRepository {
    override fun getAllFlightsStream(): Flow<List<Airport>> =
        flightSearchDao.getAllFlights()

    override fun getFlightSuggestionsStream(searchQuery: String): Flow<List<Airport>> =
        flightSearchDao.getFlightSuggestions(searchQuery)

    override fun getAirportFromIataCode(iataCode: String): Flow<Airport> =
        flightSearchDao.getAirportFromIataCode(iataCode)

    override fun getFavoritesStream(): Flow<List<Favorite>> = flightSearchDao.getFavorites()

    override suspend fun addFavorite(favorite: Favorite) = flightSearchDao.addFavorite(favorite)

    override suspend fun removeFavorite(favorite: Favorite) = flightSearchDao.removeFavorite(favorite)
}