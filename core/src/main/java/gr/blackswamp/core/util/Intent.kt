package gr.blackswamp.core.util

import android.content.Intent
import java.util.*

fun Intent.getUUIDExtra(name: String): UUID? {
    return this.getSerializableExtra(name) as? UUID
}
