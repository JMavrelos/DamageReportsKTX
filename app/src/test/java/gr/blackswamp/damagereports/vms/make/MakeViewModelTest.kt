package gr.blackswamp.damagereports.vms.make

import com.nhaarman.mockitokotlin2.reset
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.testing.AndroidKoinTest
import gr.blackswamp.core.testing.TestDispatcher
import gr.blackswamp.damagereports.data.repos.MakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module
import org.mockito.Mockito.mock
import java.util.*

@ExperimentalCoroutinesApi
class MakeViewModelTest : AndroidKoinTest() {

    val repo = mock(MakeRepository::class.java)

    override val modules = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }
    private lateinit var vm: MakeViewModelImpl

    private fun initVm(brandId: UUID?) {
        vm = MakeViewModelImpl(app, brandId)
        reset(repo)
    }


}