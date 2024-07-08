package di

import database.PayloadDatabase
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
import util.BASE_URL
import util.TOKEN

actual fun platformModule(): Module = module {
    single<PayloadDatabase> { getDatabase() }
}

actual fun ktorModule(): Module = module {
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
                header(HttpHeaders.Authorization, TOKEN)
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