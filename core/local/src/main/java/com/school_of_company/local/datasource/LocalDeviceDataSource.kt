package com.school_of_company.local.datasource

interface LocalDeviceDataSource {
    suspend fun savedDeviceToken(deviceToken: String)
    suspend fun getDeviceToken(): String
    suspend fun saveMemberId(memberId: Long)  // ← 추가
    suspend fun getMemberId(): Long           // ← 추가
}