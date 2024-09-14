package dev.alnoer.flightsearch.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FlightSearchDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :searchQuery || '%' OR name LIKE '%' || :searchQuery || '%'")
    fun getFlightSuggestions(searchQuery: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllFlights(): Flow<List<Airport>>

    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportFromIataCode(iataCode: String) : Flow<Airport>
}