package com.example.datn.utils.Extension

// File: LiveDataExtensions.kt

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object LiveDataExtensions {

    //Lần đầu gọi thì không the quan sát được dữ liệu, nhưng lần thứ 2 mới có thể xóa quan sátđu liệu lấy được
    //tu lần đầu
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

    //lần đầu gọi quan sát được dữ liệu nhưng lần thu 2 trở đi thì không xóa được dữ liệu quan sát lần trước
    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value) // Gọi hàm onChanged của Observer được truyền vào
                removeObserver(this) // Xóa Observer sau khi nhận được dữ liệu một lần
            }
        })
    }

    fun <T> LiveData<T>.observeAndHandleOnce(owner: LifecycleOwner, handleResult: (T) -> Unit) {
        var isFirstTime = true
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                if (isFirstTime) {
                    isFirstTime = false
                    handleResult(value)
                    removeObserver(this)
                }
            }
        })
    }
}
