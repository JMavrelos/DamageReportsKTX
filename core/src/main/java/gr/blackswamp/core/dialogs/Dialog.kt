package gr.blackswamp.core.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import gr.blackswamp.core.R

class Dialog : DialogFragment(), IDialog {
    companion object {
        private const val TAG = "Dialog"
        private const val POSITIVE = "$TAG.POSITIVE"
        private const val NEGATIVE = "$TAG.NEGATIVE"
        private const val NEUTRAL = "$TAG.NEUTRAL"
        private const val TITLE = "$TAG.TITLE"
        private const val RESOURCE_ID = "$TAG.RESOURCE_ID"
        private const val CANCELABLE = "$TAG.CANCELABLE"
        private const val PADDING = "$TAG.PADDING"
        private const val DATA = "$TAG.DATA"
        private const val ID = "$TAG.ID"

        const val BUTTON_POSITIVE = AlertDialog.BUTTON_POSITIVE
        const val BUTTON_NEGATIVE = AlertDialog.BUTTON_NEGATIVE
        const val BUTTON_CANCELED = 0
        const val BUTTON_NEUTRAL = AlertDialog.BUTTON_NEUTRAL

        internal fun buildTag(id: Int): String = "$TAG.$id"

        fun builder(id: Int, @LayoutRes resId: Int): IDialogBuilder = DialogBuilder(id, resId)

        private fun newInstance(
            id: Int,
            res: Int,
            title: String,
            positive: String?,
            neutral: String?,
            negative: String?,
            cancelable: Boolean,
            padding: Int,
            data: Bundle?
        ): Dialog {
            return Dialog().apply {
                arguments = Bundle().apply {
                    putInt(ID, id)
                    putInt(RESOURCE_ID, res)
                    putString(TITLE, title)
                    putString(POSITIVE, positive)
                    putString(NEUTRAL, neutral)
                    putString(NEGATIVE, negative)
                    putBoolean(CANCELABLE, cancelable)
                    putInt(PADDING, padding)
                    putBundle(DATA, data)
                }
            }
        }
    }

    private lateinit var dialogView: View
    var initViewCallback: ((View) -> Unit)? = null
    //region argument variables
    private val dialogId: Int
        get() = arguments!!.getInt(ID, -1)
    private val payload: Bundle?
        get() = arguments!!.getBundle(DATA)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        //region load necessary variables from arguments
        val title = arguments!!.getString(TITLE) ?: ""
        val positive = arguments!!.getString(POSITIVE, null)
        val negative = arguments!!.getString(NEGATIVE, null)
        val neutral = arguments!!.getString(NEUTRAL, null)
        val cancelable = arguments!!.getBoolean(CANCELABLE)
        val resId = arguments!!.getInt(RESOURCE_ID)
        val padding = arguments!!.getInt(PADDING, 0)
        //endregion

        //build the view
        dialogView = LayoutInflater.from(activity).inflate(resId, null, false)
        //apply padding
        if (padding > 0) {
            dialogView.setPadding(padding, padding, padding, padding)
        }
        //initialize if needed
        initViewCallback?.invoke(dialogView)

        //start building the dialog
        val builder = AlertDialog
            .Builder(activity!!)
            .setView(dialogView)
            .setCancelable(cancelable)
            .setOnCancelListener { act(BUTTON_CANCELED) }

        // set up title
        if (title.isNotBlank()) builder.setTitle(title)

        //region update the positive button
        if (positive != null) {
            if (positive.isNotBlank())
                builder.setPositiveButton(positive, null)
            else
                builder.setPositiveButton(android.R.string.yes, null)
        }
        //endregion

        //region update the negative button
        if (negative != null) {
            if (negative.isNotBlank()) {
                builder.setNegativeButton(negative, null)
            } else {
                builder.setNegativeButton(android.R.string.no, null)
            }
        }
        //endregion

        //region update the neutral button
        if (neutral != null) {
            if (neutral.isNotBlank()) {
                builder.setNegativeButton(neutral, null)
            } else {
                builder.setNegativeButton(android.R.string.cancel, null)
            }
        }
        //endregion
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(cancelable)
        dialog.setOnShowListener {
            if (positive != null) dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { act(BUTTON_POSITIVE) }
            if (negative != null) dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener { act(BUTTON_NEGATIVE) }
            if (neutral != null) dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener { act(BUTTON_NEUTRAL) }
        }
        if (title.isBlank())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
        return dialog
    }

    private fun act(which: Int) {
        var dismiss = true
        try {
            val listener = activity as? DialogFinishedListener ?: return
            dismiss = listener.onDialogFinished(dialogId, which, dialogView, payload)
        } finally {
            if (dismiss)
                this.dismiss()
        }
    }

    override fun show(fm: FragmentManager): String {
        val tag = buildTag(id)
        show(fm, tag)
        return tag
    }

    private class DialogBuilder(val id: Int, @LayoutRes val resId: Int) : IDialogBuilder {
        private var padding: Int = 8
        private var title: String = ""
        private var positive: String? = null
        private var negative: String? = null
        private var neutral: String? = null
        private var cancelable = true
        private var payload: Bundle? = null
        private var initViewCallback: ((View) -> Unit)? = null

        override fun setTitle(title: String): IDialogBuilder {
            this.title = title
            return this
        }

        override fun setButtons(positive: Boolean?, negative: Boolean?, neutral: Boolean?): IDialogBuilder {
            if (positive == true) {
                this.positive = ""
            }
            if (negative == true) {
                this.negative = ""
            }
            if (neutral == true) {
                this.neutral = ""
            }
            return this
        }

        override fun setButtons(positive: String?, negative: String?, neutral: String?): IDialogBuilder {
            this.positive = positive
            this.negative = negative
            this.neutral = neutral
            return this
        }

        override fun setPositive(text: String): IDialogBuilder {
            positive = text
            return this
        }

        override fun setNegative(text: String): IDialogBuilder {
            negative = text
            return this
        }

        override fun setNeutral(text: String): IDialogBuilder {
            neutral = text
            return this
        }

        override fun setCancelable(cancelable: Boolean): IDialogBuilder {
            this.cancelable = cancelable
            return this
        }

        override fun setData(data: Bundle): IDialogBuilder {
            this.payload = data
            return this
        }

        override fun setInitViewCallback(action: (View) -> Unit): IDialogBuilder {
            initViewCallback = action
            return this
        }

        override fun build(): IDialog {
            val dialog = newInstance(
                id,
                resId,
                title,
                positive,
                neutral,
                negative,
                cancelable,
                padding,
                payload
            )
            dialog.initViewCallback = initViewCallback
            return dialog
        }

        override fun show(activity: AppCompatActivity) = show(activity.supportFragmentManager)

        override fun show(fm: FragmentManager): String = build().show(fm)
    }
}