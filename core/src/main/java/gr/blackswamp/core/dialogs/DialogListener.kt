package gr.blackswamp.core.dialogs

import android.os.Bundle
import android.view.View

interface DialogListener {
    fun onDialogFinished(result: DialogResult, id: Int, dialog: View, data: Bundle?): Boolean
}