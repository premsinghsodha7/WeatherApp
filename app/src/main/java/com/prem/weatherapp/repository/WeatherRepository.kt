package com.prem.weatherapp.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.prem.weatherapp.R
import com.prem.weatherapp.database.cities.CityDatabaseDao
import com.prem.weatherapp.database.cities.DbSavedCity
import com.prem.weatherapp.database.weather.WeatherDatabaseDao
import com.prem.weatherapp.database.weather.asDomainModel
import com.prem.weatherapp.managers.LocationManager
import com.prem.weatherapp.network.WeatherApi
import com.prem.weatherapp.network.WeatherResult
import com.prem.weatherapp.states.RefreshState
import com.prem.weatherapp.utils.asDatabaseModel
import com.prem.weatherapp.utils.asDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.launch
import javax.inject.Inject


const val USER_LOCATION_CITY_ID = 0L
const val USA_COUNTRY_CODE = 241L
val SAVED_COUNTRY_ID = longPreferencesKey("saved_country_id")

class WeatherRepository @Inject constructor(
    private val weatherDb: WeatherDatabaseDao,
    private val cityDb: CityDatabaseDao,
    private val api: WeatherApi,
    private val lm: LocationManager,
    private val prefs: DataStore<Preferences>
) {

    private fun removeUserLocation() {
        CoroutineScope(Dispatchers.IO).launch {
            weatherDb.clearForecast(USER_LOCATION_CITY_ID)
        }
    }

    fun onPermissionGranted() {
        getWeather(USER_LOCATION_CITY_ID)
    }

    val currentForecast =
        weatherDb.getCurrentForecast().map {
            it?.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, null)
    val hourlyForecast =
        weatherDb.getHourlyForecast().map {
            it.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val dailyForecast =
        weatherDb.getDailyForecast().map {
            it.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val alerts = weatherDb.getAlerts().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val savedCities = cityDb.getSavedCities().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val currentCity =
        cityDb.getSelectedCityFlow().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, null)

    init {
        refreshSelectedCity()
    }

    fun refreshSelectedCity() {
        CoroutineScope(Dispatchers.IO).launch {
            getWeather(cityDb.getSelectedCity().id)
        }
    }

    private fun getWeather(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val (lat, lon) = if (cityId == USER_LOCATION_CITY_ID) {
                if (!lm.hasPermission()) {
                    _refreshState.emit(RefreshState.PermissionError)
                    removeUserLocation()
                    return@launch
                }
                lm.getLocation() ?: run {
                    _refreshState.emit(RefreshState.Error(R.string.location_unavailable_message))
                    return@launch
                }
            } else {
                cityDb.getCoordinates(cityId)
            }
            val lastRefresh = weatherDb.lastUpdated(cityId)
            if (lastRefresh != null && (System.currentTimeMillis() - lastRefresh * 1000 < 600000)) {
                _refreshState.emit(RefreshState.Loaded)
                return@launch
            }
            _refreshState.emit(RefreshState.Loading)
            api.getForecast(lat, lon).run {
                when (this) {
                    is WeatherResult.Failure -> {
                        _refreshState.emit(RefreshState.Error(R.string.network_error_message))
                    }
                    is WeatherResult.Success -> {
                        this.run {
                            weatherDb.saveForecast(
                                cityId,
                                current.asDatabaseModel(cityId, timezone),
                                hourly.asDatabaseModel(cityId, timezone),
                                daily.asDatabaseModel(cityId, timezone),
                                alerts.asDatabaseModel(cityId, timezone)
                            )
                            _refreshState.emit(RefreshState.Loaded)
                        }
                    }
                }
            }
        }
    }

    fun setCurrentCity(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(cityDb.getSavedCity(cityId))
            getWeather(cityId)
        }
    }

    //------------------------------------------------------------------------------------------

    fun addCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(DbSavedCity(id))
            getWeather(id)
        }
    }

    fun removeCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.removeCity(id)
            weatherDb.clearForecast(id)
        }
    }

    //------------------------------------------------------------------------------------------

    val countryId = prefs.data.map { it[SAVED_COUNTRY_ID] ?: USA_COUNTRY_CODE }
        .stateIn(CoroutineScope(Dispatchers.IO), Eagerly, USA_COUNTRY_CODE)

    fun setCurrentCountry(newCountryId: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            prefs.edit {
                it[SAVED_COUNTRY_ID] = newCountryId
            }
        }
    }

    private val savedCityIds =
        cityDb.getSavedCityIdsFlow().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())

    val citiesForCountry = countryId.combine(savedCityIds) { countryId, savedCitiesIds ->
        cityDb.getCitiesForCountry(countryId).asDomainModel(savedCitiesIds)
    }

    suspend fun getAllCountries() = cityDb.getAllCountries()

    fun removeCurrentCity() {
        CoroutineScope(Dispatchers.IO).launch {
            removeCity(cityDb.getSelectedCity().id)
        }
    }

    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Loading)
    val refreshState: StateFlow<RefreshState> get() = _refreshState
}
