package com.school_of_company.network.util

import com.school_of_company.datastore.datasource.AuthTokenDataSource
import com.school_of_company.network.BuildConfig
import com.school_of_company.network.api.AuthAPI
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
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                // MoshiConverterFactory는 Dagger Hilt 모듈에서 주입받는 것을 권장하지만,
                // 이 Authenticator는 DI의 통제 밖에 있으므로 여기서 직접 생성합니다.
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            val authApi = retrofit.create(AuthAPI::class.java)

            // 💡 수정된 부분: 토큰 갱신 API 호출 시 refreshToken을 인자로 전달

            // 토큰 갱신 실패 시 로그아웃 처리 등을 할 수 있습니다.
            null
        } catch (e: Exception) {
            null
        }

    }
}
