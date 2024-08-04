package com.noemi.podium.streamer.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.PodiumDatabase
import util.PODIUM_DB

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PodiumDatabase> {
    val appContext = context.applicationContext
    val filePath = appContext.getDatabasePath(PODIUM_DB)
    return Room.databaseBuilder<PodiumDatabase>(
        appContext,
        filePath.absolutePath
    ).setDriver(BundledSQLiteDriver())
}

fun getDatabase(context: Context): PodiumDatabase {
    return getDatabaseBuilder(context).build()
}