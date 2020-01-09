package gr.blackswamp.core.dialogs

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

interface IDialogBuilder {
    fun setTitle(title: String): IDialogBuilder
    fun setButtons(positive: Boolean?, negative: Boolean?, neutral: Boolean?): IDialogBuilder
    fun setPositive(text: String): IDialogBuilder
    fun setNegative(text: String): IDialogBuilder
    fun setNeutral(text: String): IDialogBuilder
    fun setCancelable(cancelable: Boolean): IDialogBuilder
    fun setData(data: Bundle): IDialogBuilder
    fun setInitViewCallback(action: (View) -> Unit): IDialogBuilder
    fun build(): IDialog
    fun show(activity: AppCompatActivity): String
    fun show(fm: FragmentManager): String
    fun setButtons(positive: String?, negative: String?, neutral: String?): IDialogBuilder
}
