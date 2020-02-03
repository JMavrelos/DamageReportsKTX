package gr.blackswamp.damagereports.ui.reports

import gr.blackswamp.damagereports.data.prefs.ThemeSetting
import java.util.*

sealed class ReportCommand {
    object ConfirmDiscard : ReportCommand()
    object ShowBrandSelection : ReportCommand()
    class ShowThemeSelection(val current: ThemeSetting) : ReportCommand()
    class ShowModelSelection(val brandId: UUID) : ReportCommand()
}