package gr.blackswamp.core.dialogs

import android.text.InputType
import android.view.View
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import gr.blackswamp.core.R

class DialogBuilders {
    companion object {
        fun messageDialogBuilder(id: Int, message: String) =
            DialogBuilder(id, R.layout.dialog_text)
                .setCancelable(false)
                .setInitViewCallback {
                    (it as TextView).text = message
                }

        fun inputDialogBuilder(id: Int, message: String, value: String, inputType: Int, singleLine: Boolean) =
            DialogBuilder(id, R.layout.dialog_input)
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

        fun extractInput(dialog: View): String? {
            return (dialog as? TextInputLayout)?.editText?.text?.toString()
        }
    }
}
