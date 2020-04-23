package gr.blackswamp.damagereports.ui.fragments

import gr.blackswamp.core.ui.CoreFragment
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.databinding.FragmentDamageBinding
import gr.blackswamp.damagereports.logic.interfaces.DamageViewModel
import gr.blackswamp.damagereports.logic.vms.DamageViewModelImpl
import org.koin.android.viewmodel.ext.android.viewModel

class DamageFragment : CoreFragment<DamageViewModel, FragmentDamageBinding>() {
    override val vm: DamageViewModel by viewModel<DamageViewModelImpl>()
    override val binding: FragmentDamageBinding by lazy { FragmentDamageBinding.inflate(layoutInflater) }
    override val optionsMenuId: Int = R.menu.damage_view

    companion object {
        fun newInstance() = DamageFragment()
    }

}
