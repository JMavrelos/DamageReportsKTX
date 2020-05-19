package gr.blackswamp.core.util

import androidx.lifecycle.MutableLiveData

fun <T> MutableLiveData<T>.refresh() {
    this.postValue(this.value)
}