package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import model.FavoriteStream
import model.PayloadData

@Database(entities = [PayloadData::class, FavoriteStream::class], version = 2, exportSchema = false)
@TypeConverters(PayloadAccountConverter::class)
abstract class PodiumDatabase: RoomDatabase() {

    abstract fun getStreamerDao(): StreamerDAO
    abstract fun getFavoriteDao(): FavoriteDAO
}