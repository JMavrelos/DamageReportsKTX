package gr.blackswamp.core.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Suppress("SameParameterValue")
abstract class CoreFragment<T : Any> : Fragment() {
    abstract val vm: T
    @get:LayoutRes
    abstract val layoutId: Int
    protected open val withOptionsMenu = false

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutId, container, false)
        setHasOptionsMenu(withOptionsMenu)
        setUpBindings(view)
        initView(savedInstanceState)
        setUpListeners()
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObservers(vm)
    }

    protected open fun setUpBindings(view: View) {}

    protected open fun initView(state: Bundle?) {}

    protected open fun setUpListeners() {}

    protected open fun setUpObservers(vm: T) {}

    /**
     * short hand method to add observers faster and avoid problems with lifecycle owner
     */
    protected fun <D> LiveData<D>.observe(observer: ((D?) -> Unit)) {
        this.observe(viewLifecycleOwner, Observer {
            observer.invoke(it)
        })
    }
}