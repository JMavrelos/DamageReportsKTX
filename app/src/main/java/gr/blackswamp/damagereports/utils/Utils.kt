package gr.blackswamp.damagereports.utils

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.SIZE_MINI
import gr.blackswamp.damagereports.R

private var miniSize: Int = 0
private var normalSize: Int = 0
private var fabMargin: Int = 0

fun FloatingActionButton.moveBy(offset: Float, max: Int) {
    val fabSize: Int = if (this.size == SIZE_MINI) {
        if (miniSize == 0) {
            miniSize = (40 * this.resources.displayMetrics.density).toInt()
        }
        miniSize
    } else {
        if (normalSize == 0) {
            normalSize = (56 * this.resources.displayMetrics.density).toInt()
        }
        normalSize
    }
    if (fabMargin == 0) {
        fabMargin = this.resources.getDimensionPixelSize(R.dimen.fab_margin)
    }

    val maxOffset = max - (fabSize / 2) - fabMargin

    val params = layoutParams as CoordinatorLayout.LayoutParams
    params.marginEnd = ((offset * maxOffset) + fabMargin).toInt()
    layoutParams = params
}