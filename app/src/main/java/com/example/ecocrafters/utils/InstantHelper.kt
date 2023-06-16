package com.example.ecocrafters.utils

import android.icu.text.MeasureFormat
import android.icu.util.MeasureUnit
import android.os.Build
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object InstantHelper {

    enum class Unit{
        YEAR,
        MONTH,
        WEEK,
        DAY,
        HOUR,
        MINUTE,
        SECOND
    }

    fun toDateString(instantString: String): String {
        val instant = Instant.parse(instantString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    fun toBetweenNowString(instantString: String): String {
        val instant1 = Instant.parse(instantString)
        val instant2 = Instant.now()
        val duration = Duration.between(instant1, instant2)
        val durationDays = duration.toDaysPart()
        val locale = Locale.getDefault()
        return when{
            durationDays.floorDiv(360)>= 1 -> {
                val yearName = unitDisplayName(locale, Unit.YEAR)
                "${durationDays.floorDiv(360)} $yearName"
            }
            durationDays.floorDiv(30)>= 1 -> {
                val monthName = unitDisplayName(locale, Unit.MONTH)
                "${durationDays.floorDiv(30)} $monthName"
            }
            durationDays.floorDiv(7)>= 1 -> {
                val weekName = unitDisplayName(locale, Unit.WEEK)
                "${durationDays.floorDiv(7)} $weekName"
            }
            durationDays >= 1 -> {
                val dayName = unitDisplayName(locale, Unit.DAY)
                "$durationDays $dayName"
            }
            duration.toHoursPart() >= 1 -> {
                val hourName = unitDisplayName(locale, Unit.HOUR)
                "${duration.toHoursPart()} $hourName"
            }
            duration.toMinutesPart() >= 1 -> {
                val minuteName = unitDisplayName(locale, Unit.MINUTE)
                "${duration.toMinutesPart()} $minuteName"
            }
            else -> {
                val secondName = unitDisplayName(locale, Unit.SECOND)
                "${duration.toSecondsPart()} $secondName"
            }
        }
    }

    fun toBetweenNowSeconds(instant: Instant): Long{
        val instantNow = Instant.now()
        val duration = Duration.between(instant, instantNow)
        return duration.toSeconds()
    }

    private fun unitDisplayName(locale: Locale, unit: Unit) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val measureFormat = MeasureFormat.getInstance(locale, MeasureFormat.FormatWidth.WIDE)
        when (unit) {
            Unit.YEAR -> measureFormat.getUnitDisplayName(MeasureUnit.YEAR)
            Unit.MONTH -> measureFormat.getUnitDisplayName(MeasureUnit.MONTH)
            Unit.WEEK -> measureFormat.getUnitDisplayName(MeasureUnit.WEEK)
            Unit.DAY -> measureFormat.getUnitDisplayName(MeasureUnit.DAY)
            Unit.HOUR -> measureFormat.getUnitDisplayName(MeasureUnit.HOUR)
            Unit.MINUTE -> measureFormat.getUnitDisplayName(MeasureUnit.MINUTE)
            Unit.SECOND -> measureFormat.getUnitDisplayName(MeasureUnit.SECOND)
        }
    } else {
        when (unit) {
            Unit.YEAR -> "year"
            Unit.MONTH -> "month"
            Unit.WEEK -> "week"
            Unit.DAY -> "day"
            Unit.HOUR -> "hour"
            Unit.MINUTE -> "min"
            Unit.SECOND -> "sec"
        }
    }
}