package gr.blackswamp.damagereports.ui.make

import android.content.Context
import android.content.Intent
import android.os.Bundle
import gr.blackswamp.core.util.getUUIDExtra
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.ActivityMakeBinding
import gr.blackswamp.damagereports.ui.base.BaseActivity
import gr.blackswamp.damagereports.ui.make.fragments.BrandFragment
import gr.blackswamp.damagereports.ui.make.fragments.ModelFragment
import gr.blackswamp.damagereports.vms.make.MakeViewModelImpl
import gr.blackswamp.damagereports.vms.make.viewmodels.MakeViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.*

class MakeActivity : BaseActivity<MakeViewModel, ActivityMakeBinding>() {
    companion object {
        const val TAG = "MakeActivity"
        private const val BRAND = "${TAG}_brand"
        fun getIntent(context: Context, brand: UUID? = null) =
            Intent(context, MakeActivity::class.java).apply {
                if (brand != null) {
                    putExtra(BRAND, brand)
                }
            }
    }

    //region bindings
    override val vm: MakeViewModel by viewModel<MakeViewModelImpl> { parametersOf(intent.getUUIDExtra(BRAND)) }
    override val binding: ActivityMakeBinding by lazy { ActivityMakeBinding.inflate(layoutInflater) }
    //endregion

    //region arguments
    private val brandId: UUID?
        get() = intent.getStringExtra(BRAND)?.let {
            UUID.fromString(it)
        }
    //endregion

    override fun initView(state: Bundle?) {
        if (state == null) {
            if (intent.getUUIDExtra(BRAND) != null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content, ModelFragment.newInstance(), ModelFragment.TAG)
                    .commit()
            } else {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.content, BrandFragment.newInstance(), BrandFragment.TAG)
                    .commit()
            }
        }
    }

    override fun setUpListeners() {}

}