package gr.blackswamp.core.widget

import android.view.View
import android.widget.TextView

var View.visible: Boolean
    get() = this.visibility == View.VISIBLE
    set(value) {
        this.visibility = if (value) View.VISIBLE else View.GONE
    }

fun TextView.updateText(text: CharSequence) {
    if (this.text.toString() != text) {
        this.setTextKeepState(text)
    }
}
