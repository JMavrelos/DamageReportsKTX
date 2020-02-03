package gr.blackswamp.damagereports.ui.reports.commands

import gr.blackswamp.damagereports.data.prefs.ThemeSetting

sealed class ReportListCommand {
    class ShowThemeSelection(val current: ThemeSetting?) : ReportListCommand()
}