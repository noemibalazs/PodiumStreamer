package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import database.PayloadAccountConverter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.Constants.STREAM_TABLE

@Serializable
@Entity(tableName = STREAM_TABLE)
data class PayloadData(

    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("url")
    val url: String,

    @SerialName("content")
    val content: String,

    @SerialName("account")
    @TypeConverters(PayloadAccountConverter::class)
    val account: PayloadAccount
)