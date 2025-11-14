package com.example.merchtools.data.util

import androidx.room.TypeConverter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal class InstantConverter {
    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun longToInstant(value: Long?): Instant? =
        value?.let(Instant::fromEpochMilliseconds)

    @OptIn(ExperimentalTime::class)
    @TypeConverter
    fun instantToLong(instant: Instant?): Long? =
        instant?.toEpochMilliseconds()
}