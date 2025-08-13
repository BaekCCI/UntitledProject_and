package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.model.mapper.toApplicationRequirement
import com.baek.untitledproject.data.sample.BoardSampleData
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.repository.ApplyRepository
import com.baek.untitledproject.domain.utils.Result
import javax.inject.Inject

class ApplyRepositoryImpl @Inject constructor() : ApplyRepository {

    override suspend fun getApplicationRequirement(postId: String): Result<ApplicationRequirements> {
        return try {

            val customQuestions = BoardSampleData.customQuestions
            val post = BoardSampleData.postList.find { it.post_id == postId }
            if (post == null) {
                return Result.Error("게시글이 존재하지 않습니다.")
            }
            val requirements = post.toApplicationRequirement(customQuestions)
            Result.Success(requirements)
        } catch (e: Exception) {
            Log.e("BoardRepository", "게시글 로딩 실패", e)
            Result.Error("존재하지 않는 공고입니다.", e)
        }
    }

    override suspend fun submitApplication(
        postId: String,
        applicationRequirements: ApplicationRequirements
    ): Result<String> {
        return try {
            //TODO: postId
            Result.Success(postId)
        } catch (e: Exception) {
            Log.e("ApplyRepositoryImpl", "지원서 제출 실패", e)
            Result.Error("지원서를 제출하는데 실패하였습니다.", e)
        }
    }

}