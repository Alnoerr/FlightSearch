package dev.alnoer.flightsearch.data

import kotlinx.coroutines.flow.Flow

class OfflineFlightSearchRepository(
    private val airportDao: AirportDao,
    private val favoriteDao: FavoriteDao
) : FlightSearchRepository {
    override fun getAllFlightsStream(): Flow<List<Airport>> =
        airportDao.getAllFlights()

    override fun getFlightSuggestionsStream(searchQuery: String): Flow<List<Airport>> =
        airportDao.getFlightSuggestions(searchQuery)

    override fun getAirportFromIataCode(iataCode: String): Flow<Airport> =
        airportDao.getAirportFromIataCode(iataCode)

    override fun getFavoritesStream(): Flow<List<Favorite>> = favoriteDao.getFavorites()

    override suspend fun addFavorite(favorite: Favorite) = favoriteDao.addFavorite(favorite)

    override suspend fun removeFavorite(departureCode: String, destinationCode: String) =
        favoriteDao.removeFavorite(departureCode, destinationCode)
}