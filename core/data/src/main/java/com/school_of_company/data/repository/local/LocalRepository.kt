package com.school_of_company.data.repository.local

interface LocalRepository {
    suspend fun savedDeviceToken(deviceToken: String)
    suspend fun getDeviceToken(): String
    suspend fun saveMemberId(memberId: Long)
    suspend fun getMemberId(): Long
}