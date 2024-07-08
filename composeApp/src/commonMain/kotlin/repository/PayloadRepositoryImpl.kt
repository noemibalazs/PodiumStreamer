package repository

import database.PayloadDAO
import kotlinx.coroutines.flow.Flow
import model.PayloadData

class PayloadRepositoryImpl(private val payloadDAO: PayloadDAO) : PayloadRepository {

    override suspend fun clearDataBase() = payloadDAO.deleteAll()

    override suspend fun deletePayloadData(id: Long) = payloadDAO.deletePayload(id)

    override fun observePayloads(): Flow<List<PayloadData>> = payloadDAO.observePayloads()

    override suspend fun savePayload(payloadData: PayloadData) = payloadDAO.insertPayload(payloadData)
}