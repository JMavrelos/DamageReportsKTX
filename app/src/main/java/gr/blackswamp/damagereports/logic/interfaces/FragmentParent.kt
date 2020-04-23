package gr.blackswamp.damagereports.logic.interfaces

interface FragmentParent {
    fun showError(message: String)
    fun showLoading(show: Boolean)
    fun hideKeyboard()
}