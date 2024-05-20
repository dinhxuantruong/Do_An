package com.example.datn.utils.Extension

// File: LiveDataExtensions.kt

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object LiveDataExtensions {
    fun <T> LiveData<T>.observeOnceAfterInit(owner: LifecycleOwner, observer: (T) -> Unit) {
        var firstObservation = true

        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                if (firstObservation) {
                    firstObservation = false
                } else {
                    removeObserver(this)
                    observer(value)
                }
            }
        })
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value) // Gọi hàm onChanged của Observer được truyền vào
                removeObserver(this) // Xóa Observer sau khi nhận được dữ liệu một lần
            }
        })
    }

}
