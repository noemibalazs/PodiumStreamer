package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import model.PayloadData
import util.PAYLOAD_TABLE

@Dao
interface PayloadDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayload(country: PayloadData)

    @Query("SELECT * FROM $PAYLOAD_TABLE")
    fun observePayloads(): Flow<List<PayloadData>>

    @Query("DELETE FROM $PAYLOAD_TABLE")
    suspend fun deleteAll()

    @Query("DELETE FROM $PAYLOAD_TABLE WHERE id = :id")
    suspend fun deletePayload(id: Long)
}