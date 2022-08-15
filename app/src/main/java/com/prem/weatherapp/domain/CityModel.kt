package com.prem.weatherapp.domain

import androidx.annotation.Keep

@Keep
data class SavedCity(
    val id: Long,
    val name: String,
    val selected: Boolean
)

@Keep
data class City(
    val id: Long,
    val name: String,
)

@Keep
data class Country(
    val id: Long,
    val name: String,
)

@Keep
data class CitySearchResult(
    val id: Long,
    val name: String,
    val saved: Boolean
)