package com.school_of_company.network.dto.reponse

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EmotionRecordResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "date") val date: String, // yyyy-MM-dd
    @Json(name = "emotion") val emotion: String,
    @Json(name = "confidence") val confidence: Int // 0~100
)

// `EmotionHistoryResponse.kt` 파일에 포함될 수도 있음
@JsonClass(generateAdapter = true)
data class EmotionHistoryResponse(
    @Json(name = "totalRecords") val totalRecords: Int,
    @Json(name = "averageConfidence") val averageConfidence: Int,
    @Json(name = "streak") val streak: Int,
    @Json(name = "emotions") val emotions: List<EmotionRecordResponse>
)