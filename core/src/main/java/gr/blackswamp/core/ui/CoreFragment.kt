package gr.blackswamp.core.ui

import android.os.Bundle
import android.view.*
import androidx.annotation.MenuRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding

abstract class CoreFragment<T : Any, V : ViewBinding> : Fragment() {
    abstract val vm: T
    abstract val binding: V

    @MenuRes
    protected open val optionsMenuId: Int = -1

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(optionsMenuId > 0)
        val view = binding.root
        initView(savedInstanceState)
        setUpListeners()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObservers(vm)
    }

    protected open fun initView(state: Bundle?) {}

    protected open fun setUpListeners() {}

    protected open fun setUpObservers(vm: T) {}

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (optionsMenuId > 0)
            inflater.inflate(optionsMenuId, menu)
        else
            super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * short hand method to add observers faster and avoid problems with lifecycle owner
     */
    protected fun <D> LiveData<D>.observe(observer: ((D?) -> Unit)) {
        this.observe(viewLifecycleOwner, Observer {
            observer.invoke(it)
        })
    }
}