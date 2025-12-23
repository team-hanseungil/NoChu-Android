package com.school_of_company.network.di

import android.content.Context
import android.util.Log
import com.school_of_company.network.api.AlertAPI
import com.school_of_company.network.api.AuthAPI
import com.school_of_company.network.api.ChatAPI
import com.school_of_company.network.api.EmotionAPI
import com.school_of_company.network.api.ImageAPI
import com.school_of_company.network.api.MemberAPI
import com.school_of_company.network.api.NoticeAPI
import com.school_of_company.network.api.PostAPI
import com.school_of_company.network.api.ReportAPI
import com.school_of_company.network.api.ReviewAPI
import com.school_of_company.network.api.WebhookAPI
import com.school_of_company.network.api.MusicAPI // 1. MusicAPI를 import 합니다.
import com.school_of_company.network.util.AuthInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message -> Log.v("HTTP", message) }
            .setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    fun provideOkhttpClient(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: com.school_of_company.network.util.TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(CookieJar.NO_COOKIES)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshiInstance(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    @Singleton
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.hanseungil.shop")
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthAPI(retrofit: Retrofit): AuthAPI =
        retrofit.create(AuthAPI::class.java)

    // 2. MusicAPI 제공 함수 추가
    @Provides
    @Singleton
    fun provideMusicAPI(retrofit: Retrofit): MusicAPI =
        retrofit.create(MusicAPI::class.java)

    @Provides
    @Singleton
    fun providePostAPI(retrofit: Retrofit): PostAPI =
        retrofit.create(PostAPI::class.java)

    @Provides
    @Singleton
    fun provideMemberAPI(retrofit: Retrofit): MemberAPI =
        retrofit.create(MemberAPI::class.java)

    @Provides
    @Singleton
    fun provideNoticeAPI(retrofit: Retrofit): NoticeAPI =
        retrofit.create(NoticeAPI::class.java)

    @Provides
    @Singleton
    fun provideReportAPI(retrofit: Retrofit): ReportAPI =
        retrofit.create(ReportAPI::class.java)

    @Provides
    @Singleton
    fun provideImageAPI(retrofit: Retrofit): ImageAPI =
        retrofit.create(ImageAPI::class.java)

    @Provides
    @Singleton
    fun provideReviewAPI(retrofit: Retrofit): ReviewAPI =
        retrofit.create(ReviewAPI::class.java)

    @Provides
    @Singleton
    fun provideWebhookAPI(retrofit: Retrofit): WebhookAPI =
        retrofit.create(WebhookAPI::class.java)

    @Provides
    @Singleton
    fun provideChatAPI(retrofit: Retrofit): ChatAPI =
        retrofit.create(ChatAPI::class.java)

    @Provides
    @Singleton
    fun provideAlertAPI(retrofit: Retrofit): AlertAPI =
        retrofit.create(AlertAPI::class.java)

    @Provides
    @Singleton
    fun provideEmotionAPI(retrofit: Retrofit): EmotionAPI =
        retrofit.create(EmotionAPI::class.java)
}