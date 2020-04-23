package gr.blackswamp.damagereports.logic.commands

import gr.blackswamp.damagereports.ui.model.Brand

sealed class ReportViewCommand {
    object ShowBrandSelect : ReportViewCommand()
    class ShowModelSelect(val brand: Brand) : ReportViewCommand()
}