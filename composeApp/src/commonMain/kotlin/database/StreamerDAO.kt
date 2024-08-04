package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.PayloadData
import util.STREAM_TABLE

@Dao
interface StreamerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStream(stream: PayloadData)

    @Query("SELECT * FROM $STREAM_TABLE")
    fun observeStreams(): Flow<List<PayloadData>>

    @Query("DELETE FROM $STREAM_TABLE")
    suspend fun deleteAll()

    @Query("DELETE FROM $STREAM_TABLE WHERE id = :id")
    suspend fun deleteStream(id: Long)
}