package room

import androidx.room.Room
import androidx.room.RoomDatabase
import database.PodiumDatabase
import platform.Foundation.NSHomeDirectory
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.instantiateImpl
import util.Constants.PODIUM_DB

fun getDatabaseBuilder(): RoomDatabase.Builder<PodiumDatabase> {
    val path = "${NSHomeDirectory()}/${PODIUM_DB}"
    return Room.databaseBuilder<PodiumDatabase>(
        name = path,
        factory = { PodiumDatabase::class.instantiateImpl() }
    ).setDriver(BundledSQLiteDriver())
}

fun getDatabase(): PodiumDatabase {
    return getDatabaseBuilder().build()
}