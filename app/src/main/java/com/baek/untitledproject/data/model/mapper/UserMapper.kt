package com.baek.untitledproject.data.model.mapper

import com.baek.untitledproject.data.local.entity.UserEntity
import com.baek.untitledproject.data.model.UserResponse
import com.baek.untitledproject.domain.data.User

fun UserResponse.toDomain(): User {
    return User(
        userId = user_id,
        email = email,
        name = name,
        birthYear = birth_year,
        gender = gender,
        department = department,
        studentId = student_id,
        termsAgreed = terms_agreed,
        privacyAgreed = privacy_agreed
    )
}

fun UserResponse.toEntity(): UserEntity {
    return UserEntity(
        userId = user_id,
        email = email,
        name = name,
        birthYear = birth_year,
        gender = gender,
        department = department,
        studentId = student_id,
        createdAt = created_at.toEpochMillis(),
        updatedAt = updated_at.toEpochMillis()
    )
}

fun User.toResponse(userId: String): UserResponse {
    return UserResponse(
        user_id = userId,
        email = email,
        name = name,
        birth_year = birthYear,
        department = department,
        student_id = studentId,
        gender = gender,
        terms_agreed = termsAgreed,
        privacy_agreed = privacyAgreed

    )
}

fun UserEntity.toDomain(): User {
    return User(
        userId = userId,
        email = email,
        name = name,
        birthYear = birthYear,
        gender = gender,
        department = department,
        studentId = studentId,
        termsAgreed = true,
        privacyAgreed = true
    )
}
