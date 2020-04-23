package gr.blackswamp.damagereports.logic.commands

import gr.blackswamp.damagereports.ui.model.Brand

sealed class BrandCommand {
    class ShowModelSelect(val brand: Brand) : BrandCommand()
}