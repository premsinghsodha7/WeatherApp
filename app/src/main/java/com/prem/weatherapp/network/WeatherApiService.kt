package com.prem.weatherapp.network

import com.prem.weatherapp.BuildConfig
import com.squareup.moshi.Moshi
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val ONE_CALL_ENDPOINT = "onecall"
interface WeatherApiService {
    @GET(ONE_CALL_ENDPOINT)
    suspend fun getForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("exclude") exclude: String = "minutely",
        @Query("appid") appId: String = "7e1c604281c1aad6e404b045e047f5cf",
    ): WeatherResult.Success
}

class WeatherApi(moshi: Moshi) {
    private val retrofit: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(WeatherApiService::class.java)
    }

    suspend fun getForecast(lat: Double, lon: Double): WeatherResult {
        return try {
            retrofit.getForecast(lat, lon)
        } catch (e: Exception) {
            WeatherResult.Failure(e.toString())
        }
    }
}
