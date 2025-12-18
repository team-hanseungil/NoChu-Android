package com.school_of_company.network.mapper.auth.request

import com.school_of_company.model.auth.request.SignUpRequestModel
import com.school_of_company.network.dto.auth.requset.SignUpRequest

fun SignUpRequestModel.toDto(): SignUpRequest =
    SignUpRequest(
        nickname = this.nickname,
        password = this.password,
    )