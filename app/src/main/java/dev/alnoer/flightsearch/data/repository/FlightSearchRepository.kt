package dev.alnoer.flightsearch.data.repository

import dev.alnoer.flightsearch.model.Airport
import dev.alnoer.flightsearch.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun getAllFlightsStream(): Flow<List<Airport>>
    fun getFlightSuggestionsStream(searchQuery: String): Flow<List<Airport>>
    fun getAirportFromIataCode(iataCode: String) : Flow<Airport>
    fun getFavoritesStream(): Flow<List<Favorite>>
    suspend fun addFavorite(favorite: Favorite)
    suspend fun removeFavorite(departureCode: String, destinationCode: String)
}