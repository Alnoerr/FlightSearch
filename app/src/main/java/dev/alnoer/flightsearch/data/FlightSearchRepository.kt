package dev.alnoer.flightsearch.data

import kotlinx.coroutines.flow.Flow

interface FlightSearchRepository {
    fun getFlightsStream(searchQuery: String): Flow<List<Airport>>
    fun getFlightSuggestionsStream(searchQuery: String): Flow<List<Airport>>
    fun getFavoritesStream(): Flow<List<Favorite>>
}