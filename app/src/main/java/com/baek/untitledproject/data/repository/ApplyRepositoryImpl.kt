package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.model.mapper.getApplicationResponse
import com.baek.untitledproject.data.model.mapper.toApplicationRequirement
import com.baek.untitledproject.data.remote.ApplyRemote
import com.baek.untitledproject.data.sample.BoardSampleData
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import com.baek.untitledproject.domain.data.QuestionAnswer
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.ApplyRepository
import com.baek.untitledproject.domain.utils.Result
import javax.inject.Inject

class ApplyRepositoryImpl @Inject constructor(
    private val applyRemote: ApplyRemote
) : ApplyRepository {

    override suspend fun getApplicationRequirement(postId: String): Result<ApplicationRequirements> {
        return try {

            val applyRequirements = applyRemote.getRequirement(postId)
            Result.Success(applyRequirements)
        } catch (e: Exception) {
            Log.e("ApplyRepository", "게시글 로딩 실패", e)
            Result.Error("존재하지 않는 공고입니다.", e)
        }
    }

    override suspend fun submitApplication(
        applicationRequirements: ApplicationRequirements,
        user: User,
        answers: List<QuestionAnswer>
    ): Result<String> {
        return try {

            val result = applyRemote.submitApplication(applicationRequirements, user, answers)
            Result.Success(result)
        } catch (e: Exception) {
            Log.e("ApplyRepositoryImpl", "지원서 제출 실패", e)
            Result.Error("지원서를 제출하는데 실패하였습니다.", e)
        }
    }

}