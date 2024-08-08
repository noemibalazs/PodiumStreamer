package service

import kotlinx.coroutines.flow.Flow
import model.Event

interface PayloadService {

    fun observePayloads(query: String, reconnectDelayMillis: Long = 3000L): Flow<Event>
}