package com.school_of_company.network.datasource.post

import com.school_of_company.network.api.PostAPI
import com.school_of_company.network.dto.post.request.PostAllRequest
import com.school_of_company.network.dto.post.request.TransactionCompleteRequest
import com.school_of_company.network.dto.post.response.AllPostDto
import com.school_of_company.network.dto.post.response.PostDto
import com.school_of_company.network.dto.post.response.PostModifyResponse
import com.school_of_company.network.util.performApiRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Post 관련 서버 통신을 담당하는 Data Source 구현체입니다.
 * PostAPI를 사용하여 실제 Retrofit 호출을 수행하고, 결과를 Flow로 래핑하여 Repository에 전달합니다.
 */
class PostDataSourceImpl @Inject constructor(
    private val postAPI: PostAPI
) : PostDataSource {

    // 1. 게시글 작성
    override fun writePostInformation(body: PostAllRequest): Flow<Unit> =
        performApiRequest { postAPI.writePostInformation(body = body) }

    // 2. 게시글 수정
    override fun modifyPostInformation(
        postId: Long,
        body: PostAllRequest
    ): Flow<Unit> =
        performApiRequest { postAPI.modifyPostInformation(
            postId = postId,
            body = body
        ) }

    // 3. 특정 게시글 상세 조회
    override fun getSpecificInformation(postId: Long): Flow<PostDto> =
        performApiRequest { postAPI.getSpecificInformation(postId = postId) }

    // 4. 모든 게시글 리스트 조회 (필터링 포함)
    override fun getAllPostInformation(type: String, mode: String): Flow<List<AllPostDto>> =
        performApiRequest { postAPI.getAllPostInformation(
            type = type,
            mode = mode
        ) }

    // 5. 내 게시글 리스트 조회 (필터링 포함)
    override fun getMyPostInformation(type: String?, mode: String?): Flow<List<AllPostDto>> =
        performApiRequest { postAPI.getMyPostInformation(
            type = type,
            mode = mode
        ) }

    // 6. 게시글 삭제
    override fun deletePostInformation(postId: Long): Flow<Unit> =
        performApiRequest { postAPI.deletePostInformation(postId = postId) }

    // 7. 거래 예약
    override fun transactionReservation(postId: Long): Flow<Unit> =
        performApiRequest { postAPI.transactionReservation(postId = postId) }

    // 8. 예약 취소
    override fun deleteReservation(postId: Long): Flow<Unit> =
        performApiRequest { postAPI.deleteReservation(postId = postId) }

    // 9. 거래 완료
    override fun transactionComplete(body: TransactionCompleteRequest): Flow<Unit> =
        performApiRequest { postAPI.transactionComplete(body = body) }

    // 10. 다른 멤버의 게시글 리스트 조회
    override fun otherPostInformation(
        type: String?,
        mode: String?,
        memberId: Long,
    ): Flow<List<AllPostDto>> =
        performApiRequest {
            postAPI.otherPostInformation(
                type = type,
                mode = mode,
                memberId = memberId
            )
        }
}