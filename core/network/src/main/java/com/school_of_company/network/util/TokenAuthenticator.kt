package com.school_of_company.network.util

import com.school_of_company.datastore.datasource.AuthTokenDataSource
import com.school_of_company.network.BuildConfig
import com.school_of_company.network.api.AuthAPI
import com.school_of_company.network.dto.auth.requset.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val dataSource: AuthTokenDataSource
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        // ⭕ 무한 루프 방지: 만약 토큰 재발급 요청(/auth/refresh) 자체가 401을 뱉은 거라면 바로 중단
        if (response.request.url.encodedPath.contains("/auth/refresh")) {
            return null
        }

        val refreshToken = runBlocking { dataSource.getRefreshToken().first() }
        val newAccessToken = refreshAccessToken(refreshToken)

        return if (newAccessToken.isNullOrEmpty()) {
            null
        } else {
            response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        return try {
            // 주입받은 OkHttpClient를 쓰면 인터셉터 때문에 꼬이므로, 재발급용 Retrofit은 별도로 빌드 (잘하셨습니다!)
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            val authApi = retrofit.create(AuthAPI::class.java)

            // AuthAPI 스펙에 맞춰 RefreshTokenRequest 전달
            val tokenResponse = runBlocking {
                authApi.refreshToken(RefreshTokenRequest(refreshToken = refreshToken))
            }

            runBlocking {
                dataSource.setAccessToken(tokenResponse.accessToken)
                dataSource.setRefreshToken(tokenResponse.refreshToken)
            }

            tokenResponse.accessToken
        } catch (e: Exception) {
            null
        }
    }
}