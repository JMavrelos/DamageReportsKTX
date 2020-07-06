
package gr.blackswamp.damagereports.data.prefs

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import gr.blackswamp.damagereports.R
import gr.blackswamp.damagereports.app.App
import timber.log.Timber

@Suppress("PrivatePropertyName")
class PreferencesImpl(app: Application) : Preferences, SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        private const val TAG = "Preferences"
    }

    private val KEY_THEME = app.getString(R.string.KEY_THEME)
    private val system_value = app.getString(R.string.system_value)
    private val dark_value = app.getString(R.string.dark_value)
    private val light_value = app.getString(R.string.light_value)
    private val auto_value = app.getString(R.string.auto_value)
    private val prefs = app.getSharedPreferences(App.PREFS_NAME, MODE_PRIVATE)

    override var themeSetting: ThemeSetting
        get() = prefs.getString(KEY_THEME, system_value).toThemeSetting()
        set(value) = prefs.edit().putString(KEY_THEME, value.toValue()).apply()

    override val themeSettingLive = MutableLiveData<ThemeSetting>()

    init {
        prefs.registerOnSharedPreferenceChangeListener(this)
        themeSettingLive.postValue(themeSetting)
    }


    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_THEME -> {
                Timber.d(themeSetting.toValue())
                themeSettingLive.postValue(themeSetting)
            }
        }
    }

    private fun String?.toThemeSetting(): ThemeSetting {
        return when (this) {
            dark_value -> ThemeSetting.Dark
            light_value -> ThemeSetting.Light
            auto_value -> ThemeSetting.Auto
            else -> ThemeSetting.System
        }
    }

    private fun ThemeSetting.toValue(): String {
        return when (this) {
            ThemeSetting.Dark -> dark_value
            ThemeSetting.Light -> light_value
            ThemeSetting.Auto -> auto_value
            ThemeSetting.System -> system_value
        }
    }
}
