package gr.blackswamp.core.dialogs

import androidx.fragment.app.FragmentManager

interface IDialog {
    fun show(fm: FragmentManager): String
}