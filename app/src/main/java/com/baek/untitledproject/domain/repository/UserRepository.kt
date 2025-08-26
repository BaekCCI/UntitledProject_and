package com.baek.untitledproject.domain.repository

import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.utils.Result

interface UserRepository {
    //유저 존재 여부 확인
    suspend fun userExists(userId: String): Result<Boolean>

    //서버 -> room 동기화
    suspend fun syncUser(userId: String): Result<User>

    //room에서 User 정보 가져오기
    suspend fun getUser(userId: String): Result<User>

    //User 저장: 서버 및 Room에 저장
    suspend fun saveUser(userId: String, user: User): Result<User>

    //로그아웃 시 로컬 데이터 삭제
    suspend fun deleteUserFromLocal(): Result<Unit>

    //회원탈퇴 시 서버/로컬 데이터 삭제
    suspend fun deleteUserFromAll(userId: String): Result<Unit>
}