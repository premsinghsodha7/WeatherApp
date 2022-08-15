package com.prem.weatherapp.states

import androidx.annotation.Keep
import androidx.annotation.StringRes

@Keep
sealed class RefreshState {
    object Loading : RefreshState()
    object Loaded : RefreshState()
    object PermissionError : RefreshState()
    data class Error(@StringRes val message: Int) : RefreshState()
}