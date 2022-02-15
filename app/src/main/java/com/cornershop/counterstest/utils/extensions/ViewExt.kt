package com.cornershop.counterstest.utils.extensions

import androidx.appcompat.widget.SearchView

fun SearchView.observeTextChange(value: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean = true
        override fun onQueryTextChange(newText: String?): Boolean {
            val query = newText.orEmpty()
            value.invoke(query)
            return true
        }
    })
}