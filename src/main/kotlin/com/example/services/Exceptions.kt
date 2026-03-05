package com.example.services

class NotFoundException(message: String) : RuntimeException(message)
class AccessDeniedException(message: String) : RuntimeException(message)
class BusinessException(message: String) : RuntimeException(message)
