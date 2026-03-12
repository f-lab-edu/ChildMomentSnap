package com.jg.childmomentsnap.core.domain.usecase

import com.jg.childmomentsnap.core.common.result.DataResult
import com.jg.childmomentsnap.core.common.result.DomainResult
import com.jg.childmomentsnap.core.domain.repository.UserRepository
import javax.inject.Inject

interface IsFirstLaunchUseCase {
    suspend fun invoke(): DomainResult<Boolean, String>
}

class IsFirstLaunchUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
) : IsFirstLaunchUseCase {
    override suspend fun invoke(): DomainResult<Boolean, String> {
        val result = userRepository.getUser()

        return when(result) {
            is DataResult.Success -> {
                //  0이면 처음 설치 사용자 이므로 true
                DomainResult.Success(result.data.id == 0L)
            }
            is DataResult.Fail -> {
                DomainResult.Fail(result.message ?: "")
            }
        }
    }
}