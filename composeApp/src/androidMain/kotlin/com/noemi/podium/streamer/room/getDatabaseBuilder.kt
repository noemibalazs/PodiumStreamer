package com.noemi.podium.streamer.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import database.PayloadDatabase
import util.PAYLOAD_DB

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PayloadDatabase> {
    val appContext = context.applicationContext
    val filePath = appContext.getDatabasePath(PAYLOAD_DB)
    return Room.databaseBuilder<PayloadDatabase>(
        appContext,
        filePath.absolutePath
    ).setDriver(BundledSQLiteDriver())
}

fun getDatabase(context: Context): PayloadDatabase {
    return getDatabaseBuilder(context).build()
}