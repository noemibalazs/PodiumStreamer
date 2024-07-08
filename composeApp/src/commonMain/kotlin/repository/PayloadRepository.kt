package repository

import kotlinx.coroutines.flow.Flow
import model.PayloadData

interface PayloadRepository {

    fun observePayloads(): Flow<List<PayloadData>>

    suspend fun savePayload(payloadData: PayloadData)

    suspend fun deletePayloadData(id: Long)

    suspend fun clearDataBase()
}