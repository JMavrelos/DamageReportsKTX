package gr.blackswamp.damagereports.ui.base

import android.view.View
import androidx.viewbinding.ViewBinding
import gr.blackswamp.core.ui.CoreFragment

abstract class BaseFragment<T : Any, V : ViewBinding> : CoreFragment<T>() {
    abstract val binding: V

    final override fun setUpBindings(): View = binding.root
}