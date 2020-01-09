package gr.blackswamp.core.widget

import android.text.Editable
import android.text.TextWatcher

class TextChangeListener(
    private val after: ((editable: Editable?) -> Unit)? = null
    , private val before: ((sequence: CharSequence?, start: Int, count: Int, after: Int) -> Unit)? = null
    , private val on: ((sequence: CharSequence?, start: Int, before: Int, count: Int) -> Unit)? = null
) : TextWatcher {
    private var ignore: Boolean = false

    fun pause() {
        ignore = true
    }

    fun resume() {
        ignore = false
    }


    override fun afterTextChanged(editable: Editable?) {
        if (!ignore) after?.invoke(editable)
    }

    override fun beforeTextChanged(sequence: CharSequence?, start: Int, count: Int, after: Int) {
        if (!ignore) before?.invoke(sequence, start, count, after)
    }


    override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
        if (!ignore) on?.invoke(sequence, start, before, count)
    }
}