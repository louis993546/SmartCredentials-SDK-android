package com.operatortokenocb.data

import io.reactivex.Completable
import okhttp3.internal.wait
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class AlertRepository(
    private val api: AlertApi,
) {
    fun sendAlert(
        email: String,
        firstName: String? = null,
        lastName: String? = null,
        location: Location? = null,
    ): Completable {
        return api.alert(
            email = email,
            name = "$firstName $lastName"
        )
    }
}


data class Location(
    val latitude: Float,
    val longitude: Float,
    val accuracy: Float,
)

interface AlertApi {
    @GET("/")
    fun alert(@Query("email") email: String, @Query("firstName") name: String): Completable
}
