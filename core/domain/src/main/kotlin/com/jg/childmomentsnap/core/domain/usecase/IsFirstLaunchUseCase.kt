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
        return when(val result = userRepository.getUser()) {
            is DataResult.Success -> {
                // 데이터가 없으면(null) 온보딩을 거치지 않은 처음 사용자이므로 true
                DomainResult.Success(result.data == null)
            }
            is DataResult.Fail -> {
                DomainResult.Fail(result.message ?: "")
            }
        }
    }
}