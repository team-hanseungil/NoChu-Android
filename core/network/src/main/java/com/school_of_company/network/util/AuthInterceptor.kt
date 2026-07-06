package com.school_of_company.network.util

import com.school_of_company.datastore.datasource.AuthTokenDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val dataSource: AuthTokenDataSource,
): Interceptor {

    private companion object {
        const val POST = "POST"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val method = request.method

        val accessToken = runBlocking { dataSource.getAccessToken().first() }

        val newRequest = when {
            // ⭕ 스포티파이 로그인/회원가입 API는 토큰 없이 요청
            (path.contains("/auth/spotify") || path.contains("/api/auth/")) && method == POST -> {
                request
            }

            path.contains("/api/sms") && method == POST -> {
                request
            }

            // ⭕ AuthAPI에 정의된 토큰 재발급 주소는 /auth/refresh (POST) 임.
            // 단, 재발급은 Authenticator 내에서 빌더를 새로 파서 처리하므로
            // 인터셉터에서는 헤더를 건드리지 않고 그대로 통과시킵니다.
            path.contains("/auth/refresh") && method == POST -> {
                request
            }

            // 그 외 모든 API 요청에는 Access Token 탑재
            else -> {
                request.newBuilder().addHeader("Authorization", "Bearer $accessToken").build()
            }
        }

        val response = chain.proceed(newRequest)

        return when (response.code) {
            204, 205 -> response.newBuilder().code(200).build()
            else -> response
        }
    }
}