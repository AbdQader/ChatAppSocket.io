package com.example.socketchat.keys

class Keys {

    companion object
    {
        // user data keys
        const val USER_ID = "userId"
        const val USER_NAME = "userName"
        const val USER_Email = "userEmail"

        // server side keys
        const val USER_JOIN = "user-join"
        const val USERS_CONNECTED = "users-connected"
        const val ROOM_MESSAGE = "room-message"
        const val REQUEST_USERS = "request-users"
        const val REMOVE_USER = "remove-user"

        // bundle keys
        const val USER_BUNDLE = "user-bundle"
        const val ROOM_USERS = "room-users"

        // message keys
        const val MESSAGE = "message"
        const val MESSAGE_CONTENT = "messageContent"
        const val SOURCE_ID = "sourceId"
        const val DESTINATION_ID = "destinationId"
        const val SOURCE_NAME = "sourceName"
        const val DESTINATION_Name = "destinationName"
        const val MESSAGE_TIME = "messageTime"

    }

}
