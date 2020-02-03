package gr.blackswamp.damagereports.data.prefs

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.damagereports.R

class PreferencesImpl(app: Application) : Preferences, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val TAG = "Preferences"
        private const val NAME = "DamageReportsPrefs"
    }

    private val KEY_THEME = app.getString(R.string.KEY_THEME)

    private val prefs = app.getSharedPreferences(NAME, MODE_PRIVATE)
    override var themeMode: ThemeMode
        get() = ThemeMode.read(prefs.getInt(KEY_THEME, 0))
        set(value) {
            prefs.edit().putInt(KEY_THEME, value.value).apply()
        }

    override val themeModeLive = MutableLiveData<ThemeMode>()

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
        themeModeLive.postValue(themeMode)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_THEME -> themeModeLive.postValue(themeMode)
        }
    }
}