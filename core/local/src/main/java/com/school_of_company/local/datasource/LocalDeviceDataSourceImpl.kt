package com.school_of_company.local.datasource

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit

class LocalDeviceDataSourceImpl @Inject constructor(
    private val shardPreferences: SharedPreferences
) : LocalDeviceDataSource {
    override suspend fun savedDeviceToken(deviceToken: String) {
        shardPreferences.edit {
            putString("device_token", deviceToken)
        }
    }

    override suspend fun getDeviceToken(): String {
        return shardPreferences.getString("device_token", "") ?: throw NullPointerException()
    }

    // ← 추가
    override suspend fun saveMemberId(memberId: Long) {
        shardPreferences.edit {
            putLong("member_id", memberId)
        }
    }

    // ← 추가
    override suspend fun getMemberId(): Long {
        return shardPreferences.getLong("member_id", 0L)
    }
}