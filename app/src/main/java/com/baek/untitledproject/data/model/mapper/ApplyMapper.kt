package com.baek.untitledproject.data.model.mapper

import com.baek.untitledproject.data.model.ApplicationResponse
import com.baek.untitledproject.domain.data.ApplicationRequirements
import com.baek.untitledproject.domain.data.User
import com.google.firebase.Timestamp


fun getApplicationResponse(
    applicationId: String,
    applicationRequirements: ApplicationRequirements,
    user: User,
    now: Timestamp
): ApplicationResponse {
    return ApplicationResponse(
        application_id = applicationId,
        applicant_user_id = user.userId!!,
        post_id = applicationRequirements.postId,
        status = "submitted",
        is_passed = null,
        interview_slot_id = null,
        interview_reservation_status = null,

        applicant_name = user.name,
        applicant_birth_year = if (applicationRequirements.requiresAge) user.birthYear else null,
        applicant_gender = if (applicationRequirements.requiresGender) user.gender else null,
        applicant_department = if (applicationRequirements.requiresDepartment) user.department else null,
        applicant_student_id = if (applicationRequirements.requiresStudentId) user.studentId else null,
        //applicant_phone = if (applicationRequirements.requiresName) user.name else null,

        post_title = applicationRequirements.postTitle,
        post_organization = applicationRequirements.postOrganization,
        post_author_user_id = applicationRequirements.postAuthorUserId,

        applied_at = now,
        updated_at = now
    )
}