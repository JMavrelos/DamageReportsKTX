package gr.blackswamp.damagereports.logic.make

import com.nhaarman.mockitokotlin2.reset
import gr.blackswamp.core.coroutines.Dispatcher
import gr.blackswamp.core.testing.AndroidKoinTest
import gr.blackswamp.core.testing.TestDispatcher
import gr.blackswamp.damagereports.data.repos.ModelRepository
import gr.blackswamp.damagereports.logic.interfaces.FragmentParent
import gr.blackswamp.damagereports.logic.vms.ModelViewModelImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module
import org.mockito.Mockito
import java.util.*

@ExperimentalCoroutinesApi
class ModelViewModelTest : AndroidKoinTest() {

    val repo = Mockito.mock(ModelRepository::class.java)
    val parent = Mockito.mock(FragmentParent::class.java)

    override val modules = module {
        single<Dispatcher> { TestDispatcher }
        single { repo }
    }
    private lateinit var vm: ModelViewModelImpl

    private fun initVm(brandId: UUID) {
        vm = ModelViewModelImpl(app, parent, brandId)
        reset(repo)
    }


}