package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.utils.Result
interface AuthRepository {
    //login
    suspend fun login(email: String, password: String): Result<String>
    //비밀번호 설정
    suspend fun setPassword(password: String): Result<Unit>

    //회원탈퇴 시 비밀번호 확인
    suspend fun reauthenticate(email: String, password: String): Result<Unit>

    //내부에서 UserRepository.clearLocal() 호출
    suspend fun logout(): Result<Unit>

    //회원 탈퇴
    suspend fun deleteAccount(): Result<Unit>
}