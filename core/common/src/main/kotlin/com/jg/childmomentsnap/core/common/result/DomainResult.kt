package com.jg.childmomentsnap.core.common.result

sealed interface DomainResult<out T, out E> {
    data class Success<T>(val data: T): DomainResult<T, Nothing>
    data class Fail<E>(val error: E): DomainResult<Nothing, E>
}