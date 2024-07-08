package database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import model.PayloadData

@Database(entities = [PayloadData::class], version = 1, exportSchema = false)
@TypeConverters(PayloadAccountConverter::class)
abstract class PayloadDatabase: RoomDatabase() {

    abstract fun getPayloadDao(): PayloadDAO
}