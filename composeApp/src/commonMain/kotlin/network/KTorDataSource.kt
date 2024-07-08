package network

import kotlinx.coroutines.flow.Flow
import model.Event

interface KTorDataSource {
    fun observePayloads(query: String, reconnectDelayMillis: Long = 3000L): Flow<Event>
}