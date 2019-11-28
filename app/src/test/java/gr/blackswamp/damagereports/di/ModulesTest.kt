package gr.blackswamp.damagereports.di

import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

class ModulesTest {

    @Test
    fun dryRun() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(applicationModule)
        }.checkModules()
    }
}