package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.repository.UserRepository
import com.jg.childmomentsnap.core.model.User
import javax.inject.Inject

interface SetUserUseCase {
    suspend fun invoke(user: User): DomainResult<Boolean, String>
}

class SetUserUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : SetUserUseCase {
    override suspend fun invoke(user: User): DomainResult<Boolean, String> {
        return when (val result = userRepository.setUser(user)) {
            is DataResult.Success -> {
                DomainResult.Success(result.data)
            }
            is DataResult.Fail -> {
                DomainResult.Fail(result.message ?: "Unknown error")
            }
        }
    }
}