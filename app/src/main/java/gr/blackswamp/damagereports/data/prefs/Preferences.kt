package gr.blackswamp.damagereports.data.prefs

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.damagereports.R

class Preferences(app: Application) : IPreferences, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val TAG = "Preferences"
        private const val NAME = "DamageReportsPrefs"
    }
    private val KEY_DARK_THEME = app.getString(R.string.KEY_DARK_THEME)
    private val prefs = app.getSharedPreferences(NAME, MODE_PRIVATE)

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
    }

    override var darkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
        }

    override val darkThemeLive = MutableLiveData<Boolean>()

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_DARK_THEME -> darkThemeLive.postValue(prefs.getBoolean(KEY_DARK_THEME,false))
        }
    }
}