package util

import model.FavoriteStream
import model.PayloadData

fun PayloadData.toFavoriteStream() = FavoriteStream(
    id = id,
    content = content,
    url = url,
    username = account.username,
    avatar = account.avatar
)