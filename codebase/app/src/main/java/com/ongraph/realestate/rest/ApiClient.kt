package com.ongraph.realestate.rest

import com.ongraph.realestate.utils.SharedPrefsHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    /*base url*/
    val BASE_URL = "https://realestatecon.herokuapp.com/api/"  //dev DB

//    val BASE_URL = "https://realestatecon.herokuapp.com/api/user" //prod DB

    private var retrofit: Retrofit? = null

    fun getClient(): Retrofit? {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()

        httpClient.addInterceptor(logging)
        httpClient.readTimeout(3000, TimeUnit.SECONDS)
        httpClient.writeTimeout(3000, TimeUnit.SECONDS)

        httpClient.interceptors().add(Interceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder().addHeader("Connection", "close")
            println("tokenn " + SharedPrefsHelper.getInstance().get("x-auth"))
            if (!SharedPrefsHelper.getInstance().get("x-auth", "null").equals("null", true)) {
                requestBuilder.addHeader(
                    "x-auth", SharedPrefsHelper.getInstance().get("x-auth")
                )
            }
            val request = requestBuilder.build()
            chain.proceed(request)
        })

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit
    }
}
