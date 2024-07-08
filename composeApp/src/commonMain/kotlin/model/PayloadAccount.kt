package model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PayloadAccount(
    @SerialName("id")
    val id: Long,

    @SerialName("avatar")
    val avatar: String,

    @SerialName("username")
    val username: String
)