package com.operatortokenocb.data

import android.content.SharedPreferences
import androidx.core.content.edit

class HackRepository(
    private val sharedPreferences: SharedPreferences,
) {
    companion object {
        const val KEY = "adslfjlasdkjfklas"
    }

    fun notifyResultHack(status: Status) {
        sharedPreferences.edit {
            putInt(KEY, status.key)
        }
    }
}

enum class Status(val key: Int) {
    Loading(0),
    Same(1),
    Different(2)
}

fun Int.toStatus() = when(this) {
    0 -> Status.Loading
    1 -> Status.Same
    2 -> Status.Different
    else -> null
}