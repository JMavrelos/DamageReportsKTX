package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData

interface ModelRepository {
    val darkThemeLive: LiveData<Boolean>

}