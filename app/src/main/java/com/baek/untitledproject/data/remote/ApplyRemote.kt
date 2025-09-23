package com.baek.untitledproject.data.remote

import com.baek.untitledproject.data.model.PostResponse
import com.baek.untitledproject.data.model.mapper.getApplicationResponse
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.CustomQuestion
import com.baek.untitledproject.domain.data.QuestionAnswer
import com.baek.untitledproject.domain.data.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ApplyRemote @Inject constructor(
    private val db: FirebaseFirestore
) {

    suspend fun getRequirement(postId: String): ApplicationRequirements = coroutineScope {

        val postDef = async {
            db.collection("posts")
                .document(postId)
                .get()
                .await()
        }

        val questionsDef = async {
            db.collection("custom_questions")
                .whereEqualTo("post_id", postId)
                .get().await()
        }

        val postDoc = postDef.await()
        val post = requireNotNull(postDoc.toObject(PostResponse::class.java)) {
            "PostResponse 매핑 실패"
        }
        val questionDoc = questionsDef.await()

        val questions = questionDoc.documents
            .sortedBy { it.getLong("question_order") ?: 0L }
            .map { doc ->
                CustomQuestion(
                    questionId = doc.getString("question_id") ?: "",
                    questionText = doc.getString("question_text") ?: ""
                )
            }


        ApplicationRequirements(
            postId = post.post_id,
            postTitle = post.title,
            postOrganization = post.organization,
            postAuthorUserId = post.author_user_id,

            requiresName = post.requires_name,
            requiresStudentId = post.requires_student_id,
            requiresDepartment = post.requires_department,
            requiresGender = post.requires_gender,
            requiresAge = post.requires_age,
            customQuestions = questions
        )
    }

    suspend fun submitApplication(
        applicationRequirements: ApplicationRequirements,
        user: User,
        answers: List<QuestionAnswer>
    ): String {
        val now = Timestamp.now()
        val applicationRef = db.collection("applications").document()
        val applicationsId = applicationRef.id

        val batch = db.batch()

        val application = getApplicationResponse(applicationsId, applicationRequirements, user, now)

        batch.set(applicationRef, application)

        answers.forEach { answer ->

            val qaRef = db.collection("question_answers").document()

            val qaData = mapOf(
                "answer_id" to qaRef.id,
                "application_id" to applicationsId,
                "question_id" to answer.questionId,
                "question_text" to answer.questionText,
                "answer_text" to answer.answerText,
                "created_at" to now
            )
            batch.set(qaRef, qaData)
        }
        batch.commit().await()
        return applicationsId
    }
}