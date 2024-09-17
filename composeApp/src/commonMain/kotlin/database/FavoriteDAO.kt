package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.FavoriteStream
import util.Constants.FAVORITE_TABLE

@Dao
interface FavoriteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteStream(stream: FavoriteStream)

    @Query("SELECT * FROM $FAVORITE_TABLE")
    fun observeFavoriteStreams(): Flow<List<FavoriteStream>>
}