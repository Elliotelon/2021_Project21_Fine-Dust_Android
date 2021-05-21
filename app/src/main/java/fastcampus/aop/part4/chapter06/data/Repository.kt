package fastcampus.aop.part4.chapter06.data

import fastcampus.aop.part4.chapter06.BuildConfig
import fastcampus.aop.part4.chapter06.data.models.monitoringstation.MonitoringStation
import fastcampus.aop.part4.chapter06.data.services.AirKoreaApiService
import fastcampus.aop.part4.chapter06.data.services.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Repository {

    suspend fun getNearbyMonitoringStation(latitude: Double, longitude: Double): MonitoringStation? {
        val tmCoordinates = kakaoLocalApiService
            .getTmCoordinates(longitude, latitude)
            .body()
            ?.documents
            ?.firstOrNull()

        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y

        if(tmX != null && tmY != null){
            return airKoreaApiService
                .getNearbyMonitoringStation(tmX, tmY)
                .body()
                ?.response
                ?.body
                ?.MonitoringStations
                ?.minBy {
                    it.tm ?: Double.MAX_VALUE
                }
        }
        return null
    }


    private val kakaoLocalApiService: KakaoLocalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create(KakaoLocalApiService::class.java)
    }

    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create(AirKoreaApiService::class.java)
    }

    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if(BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}