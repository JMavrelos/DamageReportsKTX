package gr.blackswamp.damagereports.logic.commands

import gr.blackswamp.damagereports.ui.model.Brand

sealed class ReportViewCommand {
    object ShowBrandSelect : ReportViewCommand()
    object ConfirmDiscard : ReportViewCommand()
    object MoveBack : ReportViewCommand()
    class ShowModelSelect(val brand: Brand) : ReportViewCommand()
}