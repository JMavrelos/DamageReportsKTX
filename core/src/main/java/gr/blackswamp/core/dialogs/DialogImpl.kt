package gr.blackswamp.core.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

internal class DialogImpl : DialogFragment(), IDialog {
    companion object {
        private const val TAG = "Dialog"
        private const val POSITIVE = "$TAG.POSITIVE"
        private const val NEGATIVE = "$TAG.NEGATIVE"
        private const val NEUTRAL = "$TAG.NEUTRAL"
        private const val TITLE = "$TAG.TITLE"
        private const val RESOURCE_ID = "$TAG.RESOURCE_ID"
        private const val CANCELABLE = "$TAG.CANCELABLE"
        private const val DATA = "$TAG.DATA"
        private const val ID = "$TAG.ID"

        internal fun newInstance(id: Int, res: Int, title: String, positive: String, neutral: String, negative: String, cancelable: Boolean, data: Bundle?): DialogImpl {
            return DialogImpl().apply {
                arguments = Bundle().apply {
                    putInt(ID, id)
                    putInt(RESOURCE_ID, res)
                    putString(TITLE, title)
                    putString(POSITIVE, positive)
                    putString(NEUTRAL, neutral)
                    putString(NEGATIVE, negative)
                    putBoolean(CANCELABLE, cancelable)
                    putBundle(DATA, data)
                }
            }
        }
    }

    private lateinit var dialogView: View
    private val dialogId get() = arguments!!.getInt(ID)
    var initViewCallback: ((View) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        val title = arguments!!.getString(TITLE) ?: ""
        val positive = arguments!!.getString(POSITIVE) ?: ""
        val negative = arguments!!.getString(NEGATIVE) ?: ""
        val neutral = arguments!!.getString(NEUTRAL) ?: ""
        val cancelable = arguments!!.getBoolean(CANCELABLE) ?: true
        dialogView = LayoutInflater.from(activity).inflate(arguments!!.getInt(RESOURCE_ID), null, false)
        initViewCallback?.invoke(dialogView)
        val dialog = AlertDialog
            .Builder(activity!!)
            .setView(dialogView)
            .setCancelable(cancelable)
            .setOnCancelListener(this)
            .apply {
                if (title.isNotBlank()) setTitle(title)
                if (positive.isNotBlank()) setPositiveButton(positive, null)
                if (negative.isNotBlank()) setPositiveButton(negative, null)
                if (neutral.isNotBlank()) setNeutralButton(neutral, null)
            }.create()

        dialog.setCanceledOnTouchOutside(cancelable)
        dialog.setOnShowListener {
            if (positive.isNotBlank()) dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { act(DialogResult.Positive) }
            if (negative.isNotBlank()) dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener { act(DialogResult.Negative) }
            if (neutral.isNotBlank()) dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener { act(DialogResult.Neutral) }
        }
        if (title.isBlank())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun act(result: DialogResult) {
        var dismiss = true
        try {
            val listener = activity as? DialogListener ?: return
            dismiss = listener.onDialogFinished(result, dialogId, dialogView, arguments?.getBundle(DATA))
        } finally {
            if (dismiss)
                this.dismiss()
        }
    }

    override fun show(fm: FragmentManager): String {
        val tag = "$TAG.$dialogId"
        show(fm, tag)
        return tag
    }
}