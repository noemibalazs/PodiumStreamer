package model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import util.Constants.FAVORITE_TABLE

@Serializable
@Entity(tableName = FAVORITE_TABLE)
data class FavoriteStream(
    @PrimaryKey
    @SerialName("id")
    val id: Long,

    @SerialName("url")
    val url: String,

    @SerialName("content")
    val content: String,

    @SerialName("avatar")
    val avatar: String,

    @SerialName("username")
    val username: String
)