package di

import com.mirego.konnectivity.Konnectivity
import database.PodiumDatabase
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import network.KTorDataSource
import network.KTorDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module
import repository.PayloadRepository
import repository.PayloadRepositoryImpl
import viewmodel.FavoriteViewModel
import viewmodel.StreamerViewModel

expect fun platformModule(): Module

expect fun ktorModule(): Module

fun appModule() = module {

    single<KTorDataSource> {
        val client: HttpClient = get()
        val json: Json = get()
        KTorDataSourceImpl(client, json)
    }

    single<PayloadRepository> {
        val database: PodiumDatabase = get()
        PayloadRepositoryImpl(streamerDAO = database.getStreamerDao(), favoriteDAO = database.getFavoriteDao())
    }

    single { Konnectivity() }

    factory { StreamerViewModel(get(), get(), get()) }
    factory { FavoriteViewModel(get(), get()) }
}