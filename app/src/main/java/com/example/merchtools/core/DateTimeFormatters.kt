package com.example.merchtools.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

val AuditDateTimeFormatter: DateTimeFormatter =
    DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

fun Instant.toDisplayString(): String =
    AuditDateTimeFormatter.format(this)