package com.school_of_company.data.repository.auth

import com.school_of_company.datastore.datasource.AuthTokenDataSource
import com.school_of_company.model.auth.request.EmotionResponseModel
import com.school_of_company.model.auth.request.PlaylistResponseModel
import com.school_of_company.model.auth.response.TokenResponseModel
import com.school_of_company.network.datasource.auth.AuthDataSource
import com.school_of_company.network.mapper.auth.request.toModel
import com.school_of_company.network.mapper.auth.response.toTokenResponseModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import okhttp3.MultipartBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDatasource: AuthDataSource,
    private val localDataSource: AuthTokenDataSource
) : AuthRepository {

    override fun loginWithSpotify(code: String): Flow<TokenResponseModel> {
        return remoteDatasource.loginWithSpotify(code = code).transform { response ->
            emit(response.toTokenResponseModel())
        }
    }

    override fun refreshToken(refreshToken: String): Flow<TokenResponseModel> {
        return remoteDatasource.refreshToken(refreshToken = refreshToken).transform { response ->
            emit(response.toTokenResponseModel())
        }
    }

    override fun logout(): Flow<Unit> = remoteDatasource.logout()

    override fun signLogout(): Flow<Unit> = remoteDatasource.signLogout()

    override fun musicRR(): Flow<PlaylistResponseModel> {
        return remoteDatasource.musicRR().transform { response ->
            emit(response.toModel())
        }
    }

    override fun postFace(image: MultipartBody.Part): Flow<EmotionResponseModel> {
        return remoteDatasource.postFace(image = image).transform { response ->
            emit(response.toModel())
        }
    }

    override fun getRefreshToken(): Flow<String> = localDataSource.getRefreshToken()

    override fun getAccessToken(): Flow<String> = localDataSource.getAccessToken()

    override suspend fun saveToken(token: TokenResponseModel) {
        localDataSource.setAccessToken(token.accessToken)
        localDataSource.setRefreshToken(token.refreshToken)
    }

    override suspend fun deleteTokenData() {
        localDataSource.removeAccessToken()
        localDataSource.removeRefreshToken()
        localDataSource.removeAccessTokenExp()
        localDataSource.removeRefreshTokenExp()
    }
}