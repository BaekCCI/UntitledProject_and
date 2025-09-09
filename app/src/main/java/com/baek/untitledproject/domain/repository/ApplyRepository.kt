package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import com.baek.untitledproject.domain.data.QuestionAnswer
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.utils.Result

interface ApplyRepository {
    suspend fun getApplicationRequirement(postId: String): Result<ApplicationRequirements>

    suspend fun submitApplication(
        applicationRequirements: ApplicationRequirements,
        user: User,
        answers: List<QuestionAnswer>
    ): Result<String>
}