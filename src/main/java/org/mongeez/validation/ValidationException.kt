package org.mongeez.validation

class ValidationException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(cause: Throwable) : super(cause)
}
