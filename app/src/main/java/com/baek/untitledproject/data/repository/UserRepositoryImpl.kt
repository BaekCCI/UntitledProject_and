package com.baek.untitledproject.data.repository

import android.util.Log
import com.baek.untitledproject.data.local.dao.UserDao
import com.baek.untitledproject.data.model.mapper.toDomain
import com.baek.untitledproject.data.model.mapper.toEntity
import com.baek.untitledproject.data.model.mapper.toResponse
import com.baek.untitledproject.data.remote.UserRemote
import com.baek.untitledproject.domain.data.User
import com.baek.untitledproject.domain.repository.UserRepository
import javax.inject.Inject
import com.baek.untitledproject.domain.utils.Result

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    //존재하는 유저인지
    override suspend fun userExists(userId: String): Result<Boolean> {
        return try {
            val exists = UserRemote.userExist(userId)
            Result.Success(exists)
        } catch (e: Exception) {
            Result.Error("유저 정보를 찾는 중 에러 발생", e)
        }
    }

    //서버 -> room 동기화
    override suspend fun syncUser(userId: String): Result<User> {
        return try {
            val userResponse = UserRemote.getUser(userId)
            val user = userResponse.toDomain()

            userDao.insertUser(userResponse.toEntity())
            Result.Success(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "유저 정보 동기화 실패", e)
            Result.Error("유저 정보 동기화에 실패하였습니다.", e)
        }
    }

    //room에서 User 정보 가져오기
    override suspend fun getUser(userId: String): Result<User> {
        try {
            val userEntity = userDao.getUser()
            val user = userEntity?.toDomain() ?: UserRemote.getUser(userId).toDomain()

            return Result.Success(user)
        } catch (e: Exception) {
            return Result.Error("유저 정보를 가져오는 데 실패하였습니다.", e)
        }
    }

    //User 저장: 서버 및 Room에 저장
    override suspend fun saveUser(userId: String, user: User): Result<User> {
        return try {
            val savedUser = UserRemote.saveUser(user.toResponse(userId))
            userDao.insertUser(savedUser.toEntity())
            Result.Success(savedUser.toDomain())
        } catch (e: Exception) {
            Log.e("UserRepository", "유저 정보 저장 실패", e)
            Result.Error("유저 정보를 저장하는 데 실패하였습니다.", e)
        }
    }

    //로그아웃 시 로컬 데이터 삭제
    override suspend fun deleteUserFromLocal(): Result<Unit> {
        return try {
            userDao.deleteAllData()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "유저 정보 삭제 실패", e)
            Result.Error("로그아웃에 실패하였습니다.", e)
        }
    }

    //회원탈퇴 시 서버/로컬 데이터 삭제
    override suspend fun deleteUserFromAll(userId: String): Result<Unit> {
        return try {
            UserRemote.deleteUser(userId)
            userDao.deleteAllData()
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("UserRepository", "유저 정보 삭제 실패", e)
            Result.Error("회원탈퇴에 실패하였습니다.", e)
        }
    }

}