package com.javierprado.jmapp.data.retrofit

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class RetrofitHelper private constructor() {
//    private val BASE_URL = "https://colegio-api-jma-f0c7750337fe.herokuapp.com/"
//    private val BASE_URL = "https://colegio-api.onrender.com/"
//    private val BASE_URL = "http://api-firestore-colegio.sa-east-1.elasticbeanstalk.com/"
    private val BASE_URL = "http://192.168.100.2:8090/"
    private var api: ColegioAPI
    private var bearerToken = ""
    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()
    val gson = GsonConfig.createGson()
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
//            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .client(client)
            .build()
        api = retrofit.create(ColegioAPI::class.java)
    }
    companion object {
        private val instance: RetrofitHelper by lazy { RetrofitHelper() }

        @JvmStatic
        fun getInstanceStatic(): RetrofitHelper {
            return instance
        }
    }

    fun getApi(): ColegioAPI {
        return api
    }

    fun setBearerToken(token: String) {
        bearerToken = token
    }

    fun getBearerToken(): String {
        return bearerToken
    }

    private inner class AuthInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest: Request = chain.request()
            val originalHttpUrl: HttpUrl = originalRequest.url

            val url: HttpUrl = originalHttpUrl.newBuilder().build()

            val requestBuilder: Request.Builder = originalRequest.newBuilder()
            if (bearerToken.isNotEmpty()) {
                requestBuilder.header("Authorization", bearerToken)
            }

            val newRequest: Request = requestBuilder.url(url).build()

            return chain.proceed(newRequest)
        }
    }
}