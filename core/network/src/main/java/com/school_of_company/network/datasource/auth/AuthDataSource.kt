package com.school_of_company.network.datasource.auth

import com.school_of_company.network.dto.auth.requset.LoginRequest
import com.school_of_company.network.dto.auth.requset.SignUpCertificationNumberSendRequest
import com.school_of_company.network.dto.auth.requset.SignUpRequest
import com.school_of_company.network.dto.auth.requset.SmsVerifyCodeRequest
import com.school_of_company.network.dto.reponse.EmotionResponse
import com.school_of_company.network.dto.reponse.LoginResponse
import com.school_of_company.network.dto.reponse.PlaylistResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface AuthDataSource {
    fun signUp(body: SignUpRequest): Flow<Unit>

    fun login(body: SignUpRequest): Flow<LoginResponse>

    fun postFace(memberId: Long,image: MultipartBody.Part): Flow<EmotionResponse>

    fun tokenRefresh(): Flow<LoginResponse>

    fun musicRR(memberId: Long): Flow<PlaylistResponse>

    fun signLogout(): Flow<Unit>

    fun logout(): Flow<Unit>

    fun signUpCertificationNumberCertification(body: SmsVerifyCodeRequest) : Flow<Unit>

    fun signUpCertificationNumberSend(body: SignUpCertificationNumberSendRequest) : Flow<Unit>
}