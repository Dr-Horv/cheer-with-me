package dev.fredag.cheerwithme.data.backend

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

const val BACKEND_PREFERENCES = "backendPreferences"
const val ACCESS_KEY_PREFERENCE_KEY = "accessKey"

@Module
@InstallIn(SingletonComponent::class)
object BackendModule {

    fun setAccessKey(context: Context, accessKey: String) {
        val preferences = context.getSharedPreferences(BACKEND_PREFERENCES, Context.MODE_PRIVATE)
        with (preferences.edit()) {
            putString(ACCESS_KEY_PREFERENCE_KEY, accessKey)
            commit()
        }
    }

    fun clearAccessKey(context: Context) {
        val preferences = context.getSharedPreferences(BACKEND_PREFERENCES, Context.MODE_PRIVATE)
        with (preferences.edit()) {
            remove(ACCESS_KEY_PREFERENCE_KEY)
            commit()
        }
    }

    fun hasAccessKey(context: Context): Boolean {
        val preferences = context.getSharedPreferences(BACKEND_PREFERENCES, Context.MODE_PRIVATE)
        return preferences.getString(ACCESS_KEY_PREFERENCE_KEY, "")?.isNotBlank() ?: false
    }

    @Provides
    @Singleton
    fun objectMapper(): ObjectMapper = ObjectMapper().registerKotlinModule()

    @Provides
    @Singleton
    fun retrofit(@ApplicationContext context: Context, objectMapper: ObjectMapper): Retrofit {
        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(5, TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(10, TimeUnit.SECONDS)
        okHttpClientBuilder.addInterceptor {
            val preferences = context.getSharedPreferences(BACKEND_PREFERENCES, Context.MODE_PRIVATE)
            val accessKey = preferences.getString(ACCESS_KEY_PREFERENCE_KEY, "")
            if(accessKey.isNullOrBlank()) {
                Log.w("BackendModule", "No access key")
            }
            val request = it.request().newBuilder().addHeader("Authorization", "Bearer $accessKey").build()
            it.proceed(request)
        }

        val client = okHttpClientBuilder.build()

        return Retrofit.Builder()
            .baseUrl("http://192.168.0.113:8080/")
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create(objectMapper))
            .build()
    }

    @Provides
    @Singleton
    fun backendService(retrofit: Retrofit): BackendService = retrofit.create(BackendService::class.java)

}