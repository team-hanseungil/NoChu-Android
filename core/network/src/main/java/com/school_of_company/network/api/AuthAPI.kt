package com.school_of_company.network.api

import com.school_of_company.network.dto.reponse.LoginResponse
import com.school_of_company.network.dto.auth.requset.LoginRequest
import com.school_of_company.network.dto.auth.requset.SignUpCertificationNumberSendRequest
import com.school_of_company.network.dto.auth.requset.SignUpRequest
import com.school_of_company.network.dto.auth.requset.SmsVerifyCodeRequest
import com.school_of_company.network.dto.member.response.GetAllMemberResponse
import com.school_of_company.network.dto.reponse.EmotionResponse
import com.school_of_company.network.dto.reponse.PlaylistResponse
import dagger.Module
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart // Header import 추가
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthAPI {
    @POST("/api/auth/signup")
    suspend fun signUp(
        @Body body: SignUpRequest
    )

    @POST("/api/auth/signin")
    suspend fun login(
        @Body body: SignUpRequest
    ): LoginResponse

    // 💡 수정된 부분: @Header 인자를 추가하여 RefreshToken을 명시적으로 전달
    @PATCH("/api/auth/reissue")
    suspend fun tokenRefresh(): LoginResponse

    @DELETE("/api/auth/signout")
    suspend fun logout()

    @DELETE("/api/auth/out")
    suspend fun signLogout()

    @POST("/api/sms")
    suspend fun signUpCertificationNumberSend(
        @Body body: SignUpCertificationNumberSendRequest
    )

    @POST("/api/sms/verify")
    suspend fun signUpCertificationNumberCertification(
        @Body body: SmsVerifyCodeRequest
    )

    @Multipart
    @POST("/api/emotions/{memberId}")
    suspend fun postFace(
        @Path("memberId") memberId: Long,
        @Part image: MultipartBody.Part
    ): EmotionResponse

    @POST("/api/music/{memberId}")
    suspend fun musicRR(
        @Path("memberId") memberId: Long,
    ): PlaylistResponse


}