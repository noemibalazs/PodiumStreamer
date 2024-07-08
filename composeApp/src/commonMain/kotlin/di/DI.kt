package di

import com.mirego.konnectivity.Konnectivity
import database.PayloadDatabase
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import network.KTorDataSource
import network.KTorDataSourceImpl
import org.koin.core.module.Module
import org.koin.dsl.module
import repository.PayloadRepository
import repository.PayloadRepositoryImpl
import viewmodel.PayloadViewModel

expect fun platformModule(): Module

expect fun ktorModule():Module

fun appModule() = module {

    single<KTorDataSource> {
        val client: HttpClient = get()
        val json: Json = get()
        KTorDataSourceImpl(client, json)
    }

    single<PayloadRepository> {
        val database: PayloadDatabase = get()
        PayloadRepositoryImpl(database.getPayloadDao())
    }

    single { Konnectivity() }

    factory { PayloadViewModel(get(), get(), get()) }
}