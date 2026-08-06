package com.pucetec.users.exceptions

class UserProfileNotFoundException(message: String? = null) : RuntimeException(message)
class UserProfileAlreadyExistsException(message: String? = null) : RuntimeException(message)
