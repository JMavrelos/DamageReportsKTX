package gr.blackswamp.damagereports.data.repos

import androidx.lifecycle.LiveData

interface BrandRepository {
    val darkThemeLive: LiveData<Boolean>

}