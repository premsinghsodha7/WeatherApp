package com.prem.weatherapp.network

import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
sealed class WeatherResult {
    @JsonClass(generateAdapter = true)
    data class Success(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val timezone_offset: Long,
        val current: NetworkCurrent,
        val hourly: List<NetworkHourly>,
        val daily: List<NetworkDaily>,
        val alerts: List<NetworkAlert>?,
    ) : WeatherResult()

    data class Failure(
        val error: String
    ) : WeatherResult()
}

@Keep
@JsonClass(generateAdapter = true)
data class Precipitation(
    @Json(name = "1h") val hour: Double
)

@Keep
@JsonClass(generateAdapter = true)
data class Weather(
    val id: String,
    val main: String,
    val description: String,
    val icon: String,
)

// dt properties are Unix seconds
@Keep
@JsonClass(generateAdapter = true)
data class NetworkCurrent(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "dew_point") val dewPoint: Double,
    val clouds: Double,
    val uvi: Double,
    val visibility: Double,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_gust") val windGust: Double?,
    @Json(name = "wind_deg") val windDeg: Int,
    val rain: Precipitation?,
    val snow: Precipitation?,
    val weather: List<Weather>
)

@Keep
@JsonClass(generateAdapter = true)
data class NetworkHourly(
    val dt: Long,
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "dew_point") val dewPoint: Double,
    val clouds: Double,
    val uvi: Double,
    val visibility: Double,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_gust") val windGust: Double?,
    @Json(name = "wind_deg") val windDeg: Int,
    val pop: Double,
    val rain: Precipitation?,
    val snow: Precipitation?,
    val weather: List<Weather>
)

@Keep
@JsonClass(generateAdapter = true)
data class NetworkDaily(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val moonrise: Long?,
    val moonset: Long?,
    @Json(name = "moon_phase") val moonPhase: Double,
    val temp: Temp,
    @Json(name = "feels_like") val feelsLike: FeelsLike?,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "dew_point") val dewPoint: Double,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_gust") val windGust: Double?,
    @Json(name = "wind_deg") val windDeg: Int,
    val uvi: Double,
    val pop: Double,
    val rain: Double?,
    val snow: Double?,
    val weather: List<Weather>
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Temp(
        val morn: Double,
        val day: Double,
        val eve: Double,
        val night: Double,
        val min: Double,
        val max: Double,
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class FeelsLike(
        val morn: Double,
        val day: Double,
        val eve: Double,
        val night: Double,
    )
}

@Keep
@JsonClass(generateAdapter = true)
data class NetworkAlert(
    @Json(name = "sender_name") val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)