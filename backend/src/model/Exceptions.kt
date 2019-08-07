package dev.fredag.cheerwithme.model

import java.lang.Exception

data class NotFoundException(override val message: String = ""): Exception(message)
data class UnauthorizedException(override val message: String = ""): Exception(message)