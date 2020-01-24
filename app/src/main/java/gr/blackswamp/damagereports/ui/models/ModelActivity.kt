package gr.blackswamp.damagereports.ui.models

import gr.blackswamp.core.ui.CoreActivity
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.vms.models.ModelActivityViewModel
import gr.blackswamp.damagereports.vms.models.ModelViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class ModelActivity : CoreActivity<ModelActivityViewModel>() {
    override val layoutId: Int = R.layout.activity_list
    override val vm: ModelActivityViewModel by viewModel<ModelViewModel>()
}