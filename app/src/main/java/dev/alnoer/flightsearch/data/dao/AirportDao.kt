package dev.alnoer.flightsearch.data.dao

import androidx.room.Dao
import androidx.room.Query
import dev.alnoer.flightsearch.model.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :searchQuery || '%' OR name LIKE '%' || :searchQuery || '%' ORDER BY passengers DESC")
    fun getFlightSuggestions(searchQuery: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport ORDER BY passengers DESC")
    fun getAllFlights(): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    fun getAirportFromIataCode(iataCode: String): Flow<Airport>
}