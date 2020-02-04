package gr.blackswamp.damagereports.ui.make

import android.content.Context
import android.content.Intent
import android.os.Bundle
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.ui.base.BaseActivity
import gr.blackswamp.damagereports.ui.make.fragments.BrandFragment
import gr.blackswamp.damagereports.vms.make.MakeViewModelImpl
import gr.blackswamp.damagereports.vms.make.viewmodels.MakeViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class MakeActivity : BaseActivity<MakeViewModel>() {
    companion object {
        const val TAG = "MakeActivity"
        private const val BRAND = "${TAG}_brand"
        fun getIntent(context: Context, brand: UUID? = null) =
            Intent(context, MakeActivity::class.java).apply {
                if (brand != null) {
                    putExtra(BRAND, brand.toString())
                }
            }
    }

    override val layoutId: Int = R.layout.activity_make
    override val vm: MakeViewModel by viewModel<MakeViewModelImpl>()

    //region view bindings

    //endregion

    //region arguments
    private val brandId: UUID?
        get() = intent.getStringExtra(BRAND)?.let {
            UUID.fromString(it)
        }
    //endregion

    override fun initView(state: Bundle?) {
        vm.initialize(brandId)
        if (state == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, BrandFragment.newInstance(), BrandFragment.TAG)
                .commit()
        }
    }

    override fun setUpListeners() {}

}