package com.example.ecocrafters.data.local.datastore

import java.time.Instant

data class AccessToken (
    val email: String,
    val token: String,
    val expiresIn: Int,
    val TokenCreated: Instant
)