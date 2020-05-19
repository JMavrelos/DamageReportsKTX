package gr.blackswamp.damagereports.logic.commands

import gr.blackswamp.damagereports.ui.model.Model

sealed class ModelCommand {
    class ModelSelected(val model: Model) : ModelCommand()
}