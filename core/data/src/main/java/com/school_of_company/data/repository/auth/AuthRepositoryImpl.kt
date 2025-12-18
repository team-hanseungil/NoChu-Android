package com.school_of_company.data.repository.auth

import com.school_of_company.datastore.datasource.AuthTokenDataSource
import com.school_of_company.model.auth.request.LoginRequestModel
import com.school_of_company.model.auth.request.SignUpCertificationNumberSendRequestModel
import com.school_of_company.model.auth.request.SignUpRequestModel
import com.school_of_company.model.auth.request.SmsVerifyCodeRequestModel
import com.school_of_company.model.auth.response.LoginResponseModel
import com.school_of_company.network.datasource.auth.AuthDataSource
import kotlinx.coroutines.flow.Flow
import com.school_of_company.network.mapper.auth.request.toDto
import com.school_of_company.network.mapper.auth.request.toModel
import com.school_of_company.network.mapper.auth.response.toModel
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.first // Flow.first() 사용을 위해 추가
import kotlinx.coroutines.runBlocking // Flow를 블로킹하여 값을 가져오기 위해 추가
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDatasource: AuthDataSource,
    private val localDataSource: AuthTokenDataSource
) : AuthRepository {
    override fun signIn(body: LoginRequestModel): Flow<LoginResponseModel> {
        return remoteDatasource.login(
            body = body.toDto()
        ).transform { response ->
            emit(response.toModel())
        }
    }

    override fun signUp(body: SignUpRequestModel): Flow<Unit> {
        return remoteDatasource.signUp(body = body.toDto())
    }

    override fun logout(): Flow<Unit> {
        return remoteDatasource.logout()
    }

    // 💡 수정된 부분: 토큰을 로컬에서 읽어와 DataSource에 전달
    override fun tokenRefresh(): Flow<LoginResponseModel> {
        // 1. runBlocking을 사용하여 로컬 Flow에서 RefreshToken 값을 즉시 가져옵니다.
        val refreshToken = runBlocking {
            localDataSource.getRefreshToken().first()
        }

        // 2. RefreshToken을 DataSource에 전달하여 네트워크 호출을 수행합니다.
        return remoteDatasource.tokenRefresh(refreshToken).transform { response ->
            emit(response.toModel())
        }
    }

    override fun signLogout(): Flow<Unit> {
        return remoteDatasource.signLogout()
    }

    override fun getRefreshToken(): Flow<String> {
        return localDataSource.getRefreshToken()
    }

    override suspend fun saveToken(token: LoginResponseModel) {
        localDataSource.setAccessToken(token.accessToken)
        localDataSource.setRefreshToken(token.refreshToken)
        localDataSource.setAccessTokenExp(token.accessTokenExpiresIn)
        localDataSource.setRefreshTokenExp(token.refreshTokenExpiresIn)
    }

    override suspend fun deleteTokenData() {
        localDataSource.removeAccessToken()
        localDataSource.removeRefreshToken()
        localDataSource.removeAccessTokenExp()
        localDataSource.removeRefreshTokenExp()
    }

    override suspend fun getAccessToken(): Flow<String> {
        return localDataSource.getAccessToken()
    }

    override fun signUpCertificationNumberCertification(body: SmsVerifyCodeRequestModel): Flow<Unit> {
        return remoteDatasource.signUpCertificationNumberCertification(body = body.toModel())
    }

    override fun signUpCertificationNumberSend(body: SignUpCertificationNumberSendRequestModel): Flow<Unit> {
        return remoteDatasource.signUpCertificationNumberSend(body = body.toDto())
    }
}