package room

import androidx.room.Room
import androidx.room.RoomDatabase
import database.PayloadDatabase
import platform.Foundation.NSHomeDirectory
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.instantiateImpl
import util.PAYLOAD_DB

fun getDatabaseBuilder(): RoomDatabase.Builder<PayloadDatabase> {
    val path = "${NSHomeDirectory()}/$PAYLOAD_DB"
    return Room.databaseBuilder<PayloadDatabase>(
        name = path,
        factory = { PayloadDatabase::class.instantiateImpl() }
    ).setDriver(BundledSQLiteDriver())
}

fun getDatabase(): PayloadDatabase {
    return getDatabaseBuilder().build()
}