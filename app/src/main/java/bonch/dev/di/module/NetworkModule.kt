package bonch.dev.di.module

import bonch.dev.domain.utils.Constants
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
class NetworkModule {
    private val okHttpClient = OkHttpClient
        .Builder()
        .build()

    private val gson = GsonBuilder()
        .setLenient()
        .serializeNulls()
        .create()

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}