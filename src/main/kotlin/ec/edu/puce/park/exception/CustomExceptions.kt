package ec.edu.puce.park.exception

class ResourceNotFoundException(message: String) : RuntimeException(message)

class SlotAlreadyOccupiedException(message: String) : RuntimeException(message)

class UnauthorizedAccessException(message: String) : RuntimeException(message)
