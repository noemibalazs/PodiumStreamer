package di

import com.noemi.podium.streamer.BuildKonfig.MASTODON_TOKEN
import database.PodiumDatabase
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import room.getDatabase
import util.Constants.BASE_URL

actual fun platformModule(): Module = module {
    single<PodiumDatabase> { getDatabase() }
}

actual fun serviceModule(): Module = module {
    single {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }
    }

    single {
        HttpClient(Darwin) {

            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.DEFAULT
                logger = object : Logger {
                    override fun log(message: String) {
                        co.touchlab.kermit.Logger.d(message)
                    }
                }
            }

            defaultRequest {
                header(HttpHeaders.Accept, "text/event-stream")
                header(HttpHeaders.Accept, "application/json")
                header(HttpHeaders.Authorization, "Bearer $MASTODON_TOKEN")
                header(HttpHeaders.CacheControl, "no-cache")
                header(HttpHeaders.CacheControl, "keep-alive")
                url(BASE_URL)
            }

            install(ContentNegotiation) {
                json(get())
            }
        }
    }
}