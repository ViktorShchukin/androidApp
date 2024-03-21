package com.example.test.model

import java.time.ZonedDateTime

import java.util.UUID




data class Experiment constructor(
        val name: String,
        val phoneBrand: String,
        val phoneModel: String,
        val phoneSerial: String,
        val exTimestamp: ZonedDateTime,
        val comment: String){}