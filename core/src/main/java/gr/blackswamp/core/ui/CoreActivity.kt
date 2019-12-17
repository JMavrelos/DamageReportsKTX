package gr.blackswamp.core.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import gr.blackswamp.core.R
import gr.blackswamp.core.dialogs.DialogBuilder
import gr.blackswamp.core.dialogs.DialogBuilders
import gr.blackswamp.core.dialogs.DialogListener
import gr.blackswamp.core.dialogs.DialogResult

abstract class CoreActivity<T : Any> : AppCompatActivity(), DialogListener {
    companion object {
        protected const val MESSAGE_DIALOG_ID: Int = 8008135
    }

    abstract val vm: T
    @get:LayoutRes
    abstract val layoutId: Int

    final override fun onCreate(savedInstanceState: Bundle?) {
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

    final override fun onDialogFinished(result: DialogResult, id: Int, dialog: View, data: Bundle?): Boolean {
        if (id == MESSAGE_DIALOG_ID)
            return true
        return dialogFinished(result, id, dialog, data)
    }

    protected open fun dialogFinished(result: DialogResult, id: Int, dialog: View, data: Bundle?): Boolean = true
}