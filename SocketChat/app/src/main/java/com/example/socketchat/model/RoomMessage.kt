package com.example.socketchat.model

import org.json.JSONArray
import java.util.*

data class RoomMessage (
    var message: String,
    var sourceId: String,
    var sourceName: String,
    var destinationId: JSONArray,
    var destinationName: JSONArray,
    var messageTime: Date
)