package database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import model.PayloadAccount

class PayloadAccountConverter {

    @TypeConverter
    fun toString(account: PayloadAccount): String {
        return Json.encodeToString(PayloadAccount.serializer(), account)
    }

    @TypeConverter
    fun fromString(json: String): PayloadAccount {
        return Json.decodeFromString(PayloadAccount.serializer(), json)
    }
}