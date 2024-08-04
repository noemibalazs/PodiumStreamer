package repository

import database.FavoriteDAO
import database.StreamerDAO
import kotlinx.coroutines.flow.Flow
import model.FavoriteStream
import model.PayloadData

class PayloadRepositoryImpl(private val streamerDAO: StreamerDAO, private val favoriteDAO: FavoriteDAO) : PayloadRepository {

    override suspend fun saveStream(stream: PayloadData) = streamerDAO.insertStream(stream)

    override fun observeStreams(): Flow<List<PayloadData>> = streamerDAO.observeStreams()

    override suspend fun deleteStream(id: Long) = streamerDAO.deleteStream(id)

    override suspend fun nukeStreams() = streamerDAO.deleteAll()

    override suspend fun saveFavoriteStream(stream: FavoriteStream) = favoriteDAO.insertFavoriteStream(stream)

    override fun observeFavoriteStreams(): Flow<List<FavoriteStream>> = favoriteDAO.observeFavoriteStreams()
}