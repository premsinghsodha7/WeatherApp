package com.prem.weatherapp.utils

import com.prem.weatherapp.database.cities.DbCityName
import com.prem.weatherapp.database.cities.DbSavedCityName
import com.prem.weatherapp.database.weather.DbAlert
import com.prem.weatherapp.database.weather.DbCurrent
import com.prem.weatherapp.database.weather.DbDaily
import com.prem.weatherapp.database.weather.DbHourly
import com.prem.weatherapp.domain.CitySearchResult
import com.prem.weatherapp.domain.SavedCity
import com.prem.weatherapp.network.NetworkAlert
import com.prem.weatherapp.network.NetworkCurrent
import com.prem.weatherapp.network.NetworkDaily
import com.prem.weatherapp.network.NetworkHourly
import java.util.*
import kotlin.math.roundToInt

fun NetworkCurrent.asDatabaseModel(cityId: Long, tz: String): DbCurrent {
    return DbCurrent(
        cityId = cityId,
        date = dt,
        tz = tz,
        sunrise = sunrise ?: 0,
        sunset = sunset ?: 0,
        temp = temp.roundToInt(),
        feelsLike = feelsLike.roundToInt(),
        pressure = pressure / 10,
        humidity = humidity.roundToInt(),
        dewPoint = dewPoint.roundToInt(),
        clouds = clouds.roundToInt(),
        uvi = uvi.roundToInt(),
        visibility = (visibility / 1000).roundToInt(),
        windSpeed = (windSpeed * 3.6).roundToInt(),
        windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
        windDeg = windDeg,
        icon = weather.first().icon,
        description = weather.first().description.capitalized(),
    )
}

fun List<NetworkHourly>.asDatabaseModel(cityId: Long, tz: String): List<DbHourly> {
    return map { hourly ->
        hourly.run {
            DbHourly(
                cityId = cityId,
                date = dt,
                tz = tz,
                temp = temp.roundToInt(),
                feelsLike = feelsLike.roundToInt(),
                windSpeed = (windSpeed * 3.6).roundToInt(),
                windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
                windDeg = windDeg,
                pop = (pop * 100).roundToInt(),
                rain = rain?.hour,
                snow = snow?.hour,
                icon = weather.first().icon,
                description = weather.first().description.capitalized(),
            )
        }
    }
}

@JvmName("asDatabaseModelNetworkDaily")
fun List<NetworkDaily>.asDatabaseModel(cityId: Long, tz: String): List<DbDaily> {
    return map { daily ->
        daily.run {
            DbDaily(
                cityId = cityId,
                date = dt,
                tz = tz,
                tempMin = temp.min.roundToInt(),
                tempMax = temp.max.roundToInt(),
                tempMorn = temp.morn.roundToInt(),
                tempDay = temp.day.roundToInt(),
                tempEve = temp.eve.roundToInt(),
                tempNight = temp.night.roundToInt(),
                feelsLikeMorn = feelsLike?.morn?.roundToInt(),
                feelsLikeDay = feelsLike?.day?.roundToInt(),
                feelsLikeEve = feelsLike?.eve?.roundToInt(),
                feelsLikeNight = feelsLike?.night?.roundToInt(),
                humidity = humidity.roundToInt(),
                windSpeed = (windSpeed * 3.6).roundToInt(),
                windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
                windDeg = windDeg,
                pop = (pop * 100).roundToInt(),
                rain = rain,
                snow = snow,
                icon = weather.first().icon,
                description = weather.first().description.capitalized(),
            )
        }
    }
}

@JvmName("asDatabaseModelNetworkAlert")
fun List<NetworkAlert>?.asDatabaseModel(cityId: Long, tz: String): List<DbAlert> {
    return this?.mapIndexed { i, alert ->
        alert.run {
            DbAlert(
                cityId = cityId,
                alertId = i.toLong(),
                tz = tz,
                senderName = senderName,
                event = event.capitalized(),
                start = start,
                end = end,
                description = description,
            )
        }
    } ?: emptyList()
}


fun List<DbSavedCityName>.asDomainModel(): List<SavedCity> {
    return map { c ->
        SavedCity(
            c.id,
            "${c.city}${c.state?.let { ", $it" } ?: ""}${c.country?.let { ", $it" } ?: ""}",
            c.selected
        )
    }
}


@JvmName("asDomainModelDbCityName")
fun List<DbCityName>.asDomainModel(saved: List<Long>): List<CitySearchResult> {
    return map { c ->
        CitySearchResult(
            c.id,
            "${c.city}${c.state?.let { ", $it" } ?: ""}",
            saved.binarySearch(c.id) >= 0
        )
    }
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}