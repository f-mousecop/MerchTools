package com.example.merchtools.domain.model

import java.time.Instant

data class Photo(
    val photoId: Long = 0L,
    val auditItemId: Long,
    val uri: String,
    val createdAt: Instant
)
