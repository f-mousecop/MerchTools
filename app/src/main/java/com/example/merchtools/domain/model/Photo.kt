package com.example.merchtools.domain.model

data class Photo(
    val photoId: Long = 0L,
    val auditItemId: Long,
    val uri: String,
    val createdAt: Long
)
