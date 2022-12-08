package com.example.android.politicalpreparedness.network



import android.util.Log
import com.example.android.politicalpreparedness.BuildConfig
import okhttp3.OkHttpClient

class CivicsHttpClient: OkHttpClient() {

    companion object {

         //TODO: Place your API Key Here
        const val API_KEY = BuildConfig.API_KEY

         fun getClient(): OkHttpClient {
             Log.i("CivicHttpClient", "API KEY: ${API_KEY}")
            return Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val url = original
                                .url()
                                .newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build()
                        val request = original
                                .newBuilder()
                                .url(url)
                                .build()
                        Log.i("CivicsHttpClient", "URL:${url}")
                        chain.proceed(request)
                    }
                    .build()
        }

    }

}