package gr.blackswamp.core.dialogs

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class DialogBuilder(val id: Int, @LayoutRes val res: Int) {
    private var title: String = ""
    private var positive: String = ""
    private var negative: String = ""
    private var neutral: String = ""
    private var cancelable = true
    private var data: Bundle? = null
    private var initViewCallback: ((View) -> Unit)? = null

    fun setTitle(title: String): DialogBuilder {
        this.title = title
        return this
    }

    fun setPositive(text: String): DialogBuilder {
        positive = text
        return this
    }

    fun setNegative(text: String): DialogBuilder {
        negative = text
        return this
    }

    fun setNeutral(text: String): DialogBuilder {
        neutral = text
        return this
    }

    fun setCancelable(cancelable: Boolean): DialogBuilder {
        this.cancelable = cancelable
        return this
    }

    fun setData(data: Bundle): DialogBuilder {
        this.data = data
        return this
    }

    fun setInitViewCallback(action: (View) -> Unit): DialogBuilder {
        initViewCallback = action
        return this
    }


    fun build(): IDialog {
        val dialog = DialogImpl.newInstance(
            id,
            res,
            title,
            positive,
            neutral,
            negative,
            cancelable,
            data
        )
        dialog.initViewCallback = initViewCallback
        return dialog
    }

    fun show(activity: AppCompatActivity) = show(activity.supportFragmentManager)

    fun show(fm: FragmentManager): String = build().show(fm)
}