package com.example.ecocrafters.data.local.datastore

import java.time.Instant

data class AccessToken (
    val token: String,
    val expiresIn: Int,
    val TokenCreated: Instant
)