package gr.blackswamp.core.dialogs

import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import gr.blackswamp.core.R

object DialogBuilders {

    fun messageDialogBuilder(id: Int, message: String) =
        Dialog.builder(id, R.layout.dialog_text)
            .setCancelable(false)
            .setInitViewCallback {
                (it as TextView).text = message
            }

    fun inputDialogBuilder(id: Int, message: String, value: String, inputType: Int, singleLine: Boolean) =
        Dialog.builder(id, R.layout.dialog_input)
            .setCancelable(false)
            .setInitViewCallback {
                (it as TextInputLayout).apply {
                    hint = message
                    editText!!.apply {
                        setText(value)
                        setInputType(inputType)
                        setSingleLine(singleLine)
                    }
                }
            }

    fun buildProgress(id: Int, allowCancel: Boolean = false, initialMessage: String = ""): IDialogBuilder {
        return Dialog.builder(id, R.layout.dialog_progress)
            .setButtons(false, allowCancel, false)
            .setCancelable(allowCancel)
            .setInitViewCallback { view ->
                view.findViewById<TextView>(R.id.message).text = initialMessage
            }
    }

    fun extractInput(dialog: View): String? {
        return (dialog as? TextInputLayout)?.editText?.text?.toString()
    }
}
