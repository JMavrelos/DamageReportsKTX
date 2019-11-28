package gr.blackswamp.core.widget

import androidx.appcompat.widget.SearchView

class SearchListener(val listener: (String, Boolean) -> Boolean) : SearchView.OnQueryTextListener {

    override fun onQueryTextSubmit(query: String?): Boolean = listener(query ?: "", true)

    override fun onQueryTextChange(newText: String?): Boolean = listener(newText ?: "", false)
}