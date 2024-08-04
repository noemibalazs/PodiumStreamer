package repository

import kotlinx.coroutines.flow.Flow
import model.FavoriteStream
import model.PayloadData

interface PayloadRepository {

    suspend fun saveStream(stream: PayloadData)

    fun observeStreams(): Flow<List<PayloadData>>

    suspend fun deleteStream(id: Long)

    suspend fun nukeStreams()

    suspend fun saveFavoriteStream(stream: FavoriteStream)

    fun observeFavoriteStreams(): Flow<List<FavoriteStream>>
}