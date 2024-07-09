package di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Logger.Companion.withTag
import co.touchlab.kermit.koin.KermitKoinLogger
import org.koin.core.context.startKoin
import kotlin.time.measureTime

fun initKoin() {
    val timeTaken = measureTime {
        startKoin {
            logger(
                KermitKoinLogger(withTag("koin")),
            )
            modules(
                platformModule(),
                ktorModule(),
                appModule()
            )
        }
    }.inWholeMilliseconds

    Logger.v("initKoin") { "It took $timeTaken milliseconds" }
}