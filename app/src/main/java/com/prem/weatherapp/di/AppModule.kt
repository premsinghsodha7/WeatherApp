package com.prem.weatherapp.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.prem.weatherapp.database.cities.CityDatabaseDao
import com.prem.weatherapp.database.weather.WeatherDatabaseDao
import com.prem.weatherapp.network.WeatherApi
import com.prem.weatherapp.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.prem.weatherapp.database.WeatherDatabase
import com.prem.weatherapp.managers.LocationManager
import com.prem.weatherapp.ui.forecast.dataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun weatherDatabase(app: Application) = WeatherDatabase.getInstance(app)

    @Provides
    @Singleton
    fun cityDao(db: WeatherDatabase) = db.cityDatabaseDao

    @Provides
    @Singleton
    fun weatherDao(db: WeatherDatabase) = db.weatherDatabaseDao

    @Provides
    @Singleton
    fun moshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun weatherApi(moshi: Moshi) = WeatherApi(moshi)

    @Provides
    @Singleton
    fun prefs(app: Application) = app.applicationContext.dataStore

    @Provides
    @Singleton
    fun locationProvider(app: Application): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(app)


    @Provides
    @Singleton
    fun locationManager(
        fusedLocationClient: FusedLocationProviderClient,
    ) = LocationManager(fusedLocationClient)
}
