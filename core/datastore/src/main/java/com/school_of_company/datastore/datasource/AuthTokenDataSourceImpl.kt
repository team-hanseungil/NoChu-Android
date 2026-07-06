package com.school_of_company.datastore.datasource

import androidx.datastore.core.DataStore
import com.school_of_company.datastore.AuthToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthTokenDataSourceImpl @Inject constructor(
    private val authToken: DataStore<AuthToken>
) : AuthTokenDataSource {

    override fun getAccessToken(): Flow<String> = authToken.data.map {
        it.accessToken
    }

    override suspend fun setAccessToken(accessToken: String) {
        android.util.Log.d("TOKEN_SAVE_CHECK", "Access Token 저장 시도: $accessToken")
        authToken.updateData {
            it.toBuilder().setAccessToken(accessToken).build()
        }
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        // 🔴 Refresh Token 저장 로그 (매개변수명 완벽 일치)
        android.util.Log.d("TOKEN_SAVE_CHECK", "Refresh Token 저장 시도: $refreshToken")
        authToken.updateData {
            it.toBuilder().setRefreshToken(refreshToken).build()
        }
    }

    override suspend fun removeAccessToken() {
        authToken.updateData {
            it.toBuilder().clearAccessToken().build()
        }
    }

    override fun getAccessTokenExp(): Flow<String> = authToken.data.map {
        it.accessExpiresIn
    }

    override suspend fun setAccessTokenExp(accessTokenExp: String) {
        authToken.updateData {
            it.toBuilder().setAccessExpiresIn(accessTokenExp).build()
        }
    }

    override suspend fun removeAccessTokenExp() {
        authToken.updateData {
            it.toBuilder().clearAccessExpiresIn().build()
        }
    }

    override fun getRefreshToken(): Flow<String> = authToken.data.map {
        it.refreshToken
    }

    override suspend fun removeRefreshToken() {
        authToken.updateData {
            it.toBuilder().clearRefreshToken().build()
        }
    }

    override fun getRefreshTokenExp(): Flow<String> = authToken.data.map {
        it.refreshExpiresIn
    }

    override suspend fun setRefreshTokenExp(refreshTokenExp: String) {
        authToken.updateData {
            it.toBuilder().setRefreshExpiresIn(refreshTokenExp).build()
        }
    }

    override suspend fun removeRefreshTokenExp() {
        authToken.updateData {
            it.toBuilder().clearRefreshExpiresIn().build()
        }
    }

    override fun getMemberId(): Flow<Long> = authToken.data.map {
        it.memberId
    }

    override suspend fun setMemberId(memberId: Long) {
        authToken.updateData {
            it.toBuilder().setMemberId(memberId).build()
        }
    }

    override suspend fun removeMemberId() {
        authToken.updateData {
            it.toBuilder().setMemberId(0L).build()
        }
    }
}