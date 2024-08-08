package di

import com.mirego.konnectivity.Konnectivity
import database.PodiumDatabase
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import service.PayloadService
import service.PayloadServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module
import repository.PayloadRepository
import repository.PayloadRepositoryImpl
import viewmodel.FavoriteViewModel
import viewmodel.StreamerViewModel

expect fun platformModule(): Module

expect fun serviceModule(): Module

fun appModule() = module {

    single<PayloadService> {
        val client: HttpClient = get()
        val json: Json = get()
        PayloadServiceImpl(client, json)
    }

    single<PayloadRepository> {
        val database: PodiumDatabase = get()
        PayloadRepositoryImpl(streamerDAO = database.getStreamerDao(), favoriteDAO = database.getFavoriteDao())
    }

    single { Konnectivity() }

    factory { StreamerViewModel(get(), get(), get()) }
    factory { FavoriteViewModel(get(), get()) }
}