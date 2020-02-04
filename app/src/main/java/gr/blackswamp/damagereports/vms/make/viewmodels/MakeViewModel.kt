package gr.blackswamp.damagereports.vms.make.viewmodels

import gr.blackswamp.damagereports.vms.base.IBaseViewModel
import java.util.*

interface MakeViewModel : IBaseViewModel {

    fun initialize(brandId: UUID?)
}