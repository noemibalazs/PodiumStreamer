package service

import com.noemi.podium.streamer.BuildKonfig.MASTODON_TOKEN
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.HttpStatement
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import model.Event
import model.EventType
import model.PayloadData
import model.toEventType
import util.BASE_URL

class PayloadServiceImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : PayloadService {

    override fun observePayloads(query: String, reconnectDelayMillis: Long): Flow<Event> = flow {
        coroutineScope {

            while (isActive) {
                prepareRequest(query).execute { response ->
                    if (!response.status.isSuccess()) {
                        println("Unauthorized error")
                    }
                    if (!response.isEventStream()) {
                        println("No event stream error")
                    }
                    response.bodyAsChannel()
                        .readSSEvent(
                            onSseEvent = { event ->
                                emit(event)
                            }
                        )
                }

                delay(reconnectDelayMillis)
            }
        }
    }

    private suspend fun prepareRequest(query: String): HttpStatement =
        httpClient.prepareGet(BASE_URL) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $MASTODON_TOKEN")
            }
            this.url.parameters.append("q", query)
        }

    private fun HttpResponse.isEventStream(): Boolean {
        val contentType = contentType() ?: return false
        return contentType.contentType == "text" && contentType.contentSubtype == "event-stream"
    }

    private suspend inline fun ByteReadChannel.readSSEvent(onSseEvent: (Event) -> (Unit)) {
        var event = Event()

        while (!isClosedForRead) {
            val line = readUTF8Line()

            when {
                line?.startsWith("event:") == true -> {
                    val name = line.substring(6).trim()
                    event = event.copy(type = name.toEventType())
                }

                line?.startsWith("data:") == true -> {
                    val data = line.substring(5).trim()

                    if (event.type == EventType.UPDATE || event.type == EventType.STATUS_UPDATE) {
                        val payload = json.decodeFromString<PayloadData>(data)
                        event = event.copy(payload = payload)
                    }

                    if (event.type == EventType.DELETE) {
                        event = event.copy(id = data.toLong())
                    }
                }

                else -> {
                    onSseEvent(event)
                    event = Event()
                }
            }
        }
    }
}