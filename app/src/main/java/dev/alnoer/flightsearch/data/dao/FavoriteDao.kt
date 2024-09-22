package dev.alnoer.flightsearch.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.alnoer.flightsearch.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>

    @Insert
    suspend fun addFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun removeFavorite(departureCode: String, destinationCode: String)
}