package gr.blackswamp.damagereports.ui.brands

import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.vms.brands.BrandActivityViewModel
import gr.blackswamp.damagereports.vms.brands.BrandViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class BrandActivity : CoreActivity<BrandActivityViewModel>() {
    override val layoutId: Int = R.layout.activity_list
    override val vm: BrandActivityViewModel by viewModel<BrandViewModel>()
}