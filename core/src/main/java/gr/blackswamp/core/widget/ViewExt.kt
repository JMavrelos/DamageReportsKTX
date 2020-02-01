package gr.blackswamp.core.widget

import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar

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

/**
 * Shorthand to create a view click listener and ignore the supplied view
 */
fun View.onClick(listener: () -> Unit) {
    this.setOnClickListener { listener.invoke() }
}

/**
 * Shorthand to create a menu item click listener and ignore the supplied menu item
 */
fun MenuItem.onClick(listener: () -> Unit) {
    this.setOnMenuItemClickListener {
        listener.invoke()
        true
    }
}

/**
 * Shorthand to create a navigation click listener and ignore the supplied view
 */
fun Toolbar.onNavigationClick(listener: () -> Unit) {
    this.setNavigationOnClickListener {
        listener.invoke()
    }
}