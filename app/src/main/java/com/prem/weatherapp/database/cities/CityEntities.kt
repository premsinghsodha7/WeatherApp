package com.prem.weatherapp.database.cities

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.prem.weatherapp.domain.CitySearchResult
import com.prem.weatherapp.domain.SavedCity

@Keep
@Entity(
    tableName = "saved_cities",
    foreignKeys = [
        ForeignKey(
            entity = DbCity::class,
            parentColumns = ["id"],
            childColumns = ["id"],
        )
    ],
)
data class DbSavedCity(
    @PrimaryKey
    val id: Long,
    val selected: Boolean = true
)

@Keep
@Entity(
    tableName = "cities",
    foreignKeys = [
        ForeignKey(
            entity = DbState::class,
            parentColumns = ["id"],
            childColumns = ["stateId"],
        ),
        ForeignKey(
            entity = DbCountry::class,
            parentColumns = ["id"],
            childColumns = ["countryId"],
        )
    ],
)
data class DbCity(
    @PrimaryKey
    val id: Long,
    val name: String,
    @ColumnInfo(index = true)
    val stateId: Long?,
    @ColumnInfo(index = true)
    val countryId: Long?,
    val lat: Double,
    val lon: Double,
)

@Keep
@Entity(tableName = "states")
data class DbState(
    @PrimaryKey
    val id: Long,
    val name: String
)

@Keep
@Entity(tableName = "countries")
data class DbCountry(
    @PrimaryKey
    val id: Long,
    val name: String
)

@Keep
data class Coord(
    val lat: Double,
    val lon: Double
)

@Keep
data class DbSavedCityName(
    val id: Long,
    val city: String,
    val state: String?,
    val country: String?,
    val selected: Boolean
)

@Keep
data class DbCityName(
    val id: Long,
    val city: String,
    val state: String?,
)
