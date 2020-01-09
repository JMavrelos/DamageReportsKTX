package gr.blackswamp.core.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.dialogs.DialogBuilders
import gr.blackswamp.core.dialogs.DialogFinishedListener

abstract class CoreActivity<T : Any> : AppCompatActivity(), DialogFinishedListener {
    companion object {
        protected const val MESSAGE_DIALOG_ID: Int = 8008135
    }

    abstract val vm: T
    @get:LayoutRes
    abstract val layoutId: Int
    @StyleRes
    open val theme: Int? = null

    final override fun onCreate(savedInstanceState: Bundle?) {
        theme?.let {
            setTheme(it)
        }
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        setUpBindings()
        initView(savedInstanceState)
        setUpListeners()
        setUpObservers(vm)
    }

    protected open fun setUpBindings() {}

    protected open fun initView(state: Bundle?) {}

    protected open fun setUpListeners() {}

    protected open fun setUpObservers(vm: T) {}

    protected fun showMessage(message: String) {
        DialogBuilders.messageDialogBuilder(MESSAGE_DIALOG_ID, message).show(this)
    }

    protected fun showToast(message: String, duration: Int) {
        Toast.makeText(this, message, duration).show()
    }

    protected fun showSnackBar(view: View, message: String, duration: Int) {
        Snackbar.make(view, message, duration).let { sb ->
            if (duration == Snackbar.LENGTH_INDEFINITE) {
                sb.setAction(android.R.string.ok) {
                    sb.dismiss()
                }
            }
            sb.show()
        }
    }

    final override fun onDialogFinished(id: Int, which: Int, dialog: View, payload: Bundle?): Boolean {
        if (id == MESSAGE_DIALOG_ID)
            return true
        return dialogFinished(id, which, dialog, payload)
    }

    protected open fun dialogFinished(id: Int, which: Int, dialog: View, payload: Bundle?): Boolean = true

    protected fun hideKeyboard() {
        try {
            currentFocus?.windowToken?.let {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
                    ?.hideSoftInputFromWindow(it, 0)
            }
        } catch (ignored: Throwable) {
        }
    }

}