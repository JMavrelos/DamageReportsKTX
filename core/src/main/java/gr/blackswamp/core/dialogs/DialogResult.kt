package gr.blackswamp.core.dialogs

import android.util.SparseArray

enum class DialogResult constructor(val value: Int) {
    Canceled(0),
    Negative(1),
    Neutral(2),
    Positive(3);

    companion object {
        private val EnumMap = SparseArray<DialogResult>()

        init {
            for (dialogResult in values())
                EnumMap.put(dialogResult.value, dialogResult)
        }

        fun valueOf(value: Int): DialogResult {
            return EnumMap.get(value) as DialogResult
        }
    }

}
