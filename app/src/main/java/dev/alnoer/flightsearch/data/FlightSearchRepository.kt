package dev.alnoer.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun getAllFlightsStream(): Flow<List<Airport>>
    fun getFlightSuggestionsStream(searchQuery: String): Flow<List<Airport>>
    fun getAirportFromIataCode(iataCode: String) : Flow<Airport>
    fun getFavoritesStream(): Flow<List<Favorite>>
}