package com.school_of_company.data.di // 👈 현재 RepositoryModule이 있는 패키지명

import com.school_of_company.data.repository.alert.AlertRepository
import com.school_of_company.data.repository.alert.AlertRepositoryImpl
import com.school_of_company.data.repository.auth.AuthRepository
import com.school_of_company.data.repository.auth.AuthRepositoryImpl
import com.school_of_company.data.repository.chat.ChatRepository
import com.school_of_company.data.repository.chat.ChatRepositoryImpl
import com.school_of_company.data.repository.image.ImageRepository
import com.school_of_company.data.repository.image.ImageRepositoryImpl
import com.school_of_company.data.repository.local.LocalRepository
import com.school_of_company.data.repository.local.LocalRepositoryImpl
import com.school_of_company.data.repository.member.MemberRepository
import com.school_of_company.data.repository.member.MemberRepositoryImpl
import com.school_of_company.data.repository.music.MusicRepository
import com.school_of_company.data.repository.music.MusicRepositoryImpl
import com.school_of_company.data.repository.notice.NoticeRepository
import com.school_of_company.data.repository.notice.NoticeRepositoryImpl
import com.school_of_company.data.repository.post.EmotionRepository
import com.school_of_company.data.repository.post.EmotionRepositoryImpl
import com.school_of_company.data.repository.post.PostRepository
import com.school_of_company.data.repository.post.PostRepositoryImpl
import com.school_of_company.data.repository.report.ReportRepository
import com.school_of_company.data.repository.report.ReportRepositoryImpl
import com.school_of_company.data.repository.review.ReviewRepository
import com.school_of_company.data.repository.review.ReviewRepositoryImpl
import com.school_of_company.network.datasource.music.MusicDataSource
import com.school_of_company.network.datasource.music.MusicDataSourceImpl
import com.school_of_company.network.datasource.post.EmotionDataSource
import com.school_of_company.network.datasource.post.EmotionDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindLocalRepository(
        localRepositoryImpl: LocalRepositoryImpl
    ): LocalRepository

    @Binds
    @Singleton
    abstract fun bindMemberRepository(
        memberRepositoryImpl: MemberRepositoryImpl
    ): MemberRepository

    @Binds
    @Singleton
    abstract fun bindNoticeRepository(
        noticeRepositoryImpl: NoticeRepositoryImpl
    ): NoticeRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(
        reviewRepositoryImpl: ReviewRepositoryImpl
    ): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(
        alertRepositoryImpl: AlertRepositoryImpl
    ): AlertRepository

    @Binds
    @Singleton
    abstract fun bindEmotionRepository(
        emotionRepositoryImpl: EmotionRepositoryImpl
    ): EmotionRepository

    @Binds
    @Singleton
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository
    
    @Binds
    @Singleton
    abstract fun bindEmotionDataSource(
        emotionDataSourceImpl: EmotionDataSourceImpl
    ): EmotionDataSource

    @Binds
    @Singleton
    abstract fun bindMusicDataSource(
        musicDataSourceImpl: MusicDataSourceImpl
    ): MusicDataSource
}