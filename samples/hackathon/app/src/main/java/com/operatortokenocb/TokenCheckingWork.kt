package com.operatortokenocb

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.operatortokenocb.contentprovider.ContentProvider
import com.operatortokenocb.contentprovider.TransactionTokenDecrypt
import com.operatortokenocb.data.*
import com.operatortokenocb.network.BaseRetrofitClient
import com.operatortokenocb.network.GetBearerBody
import com.operatortokenocb.network.PartnerManagementApi
import com.operatortokenocb.network.RetrofitClient
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.internal.wait
import retrofit2.Retrofit
import retrofit2.create
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TokenCheckingWork(
    appContext: Context,
    workerParams: WorkerParameters,
) : RxWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "dakwjfhklwjhf"
    }

    private val sharePref = applicationContext.getSharedPreferences("fuck", Context.MODE_PRIVATE)
    override fun createWork(): Single<Result> {
        Timber.tag("TCW").d("work running")
        val hackRepo = HackRepository(sharePref)

        if (hackRepo.isAlertEnabled()) {
            val api = getTokenApi()
            return api
                .observeAccessToken("Hackaton-Sample-App-0ae7264a-0f3d-4859-a9aa-97788446e9e2")
//            .delay(10, TimeUnit.SECONDS)
                .flatMap { accessToken ->
                    hackRepo.notifyResultHack(Status.Loading)

                    Timber.tag("TCW").d("10")
                    val body = GetBearerBody(accessToken, null, applicationContext.packageName)
                    api.observeBearerToken(body)
                }
                .retry(10)
                .flatMap { bearerToken ->

                    Timber.tag("TCW").d("20")
                    ContentProvider(applicationContext)
                        .getTransactionToken(bearerToken, "tphonehack", "psi")
                }
                .retry(10)
                .map { token ->

                    Timber.tag("TCW").d("30")
                    val data = TransactionTokenDecrypt(applicationContext).getClaimsFromTransactionToken(token)
                    Timber.tag("TCW").d(data)
                    data
                }
                .flatMap {
                    val token = Gson().fromJson(it, Token::class.java)

                    val tokenRepo = TokenRepository(sharePref)
                    val contactRepo = ContactRepository(sharePref)
                    val info = contactRepo.getInfo()
                    val email = info?.email
                    Timber.tag("TCW").d(email)

                    val oldToken = tokenRepo.getToken()
                    Timber.tag("TCW").d("old token: $oldToken")
                    Timber.tag("TCW").d("new token: ${token.psi}")
                    if (oldToken.isNullOrBlank()) {


                        Timber.tag("TCW").d("storing token ${token.psi}")
                        // {"imsi":"262011108829492","msisdn":"4915119492789","psi":"gxEGrbpp76Ty1VRn8pp4fbzkVEQSlxNF","aud":"tphonehack","iat":1674396931569}
                        tokenRepo.storeToken(token.psi)
                        hackRepo.notifyResultHack(Status.Same)
                        Observable.just(token.psi)
                    } else if (oldToken == token.psi) {
                        Timber.tag("TCW").d("same sim")
                        hackRepo.notifyResultHack(Status.Same)
                        Observable.just(token.psi)
                    } else if (email?.isNotBlank() == true) {
                        Timber.tag("TCW").d("different sim")
                        hackRepo.notifyResultHack(Status.Different)
                        AlertRepository(getAlertApi()).sendAlert(
                            email = email,
                            lastName = info.lastName,
                            firstName = info.firstName,
                        ).toObservable()
                    } else {
                        Timber.tag("TCW").d("wtf")
                        Observable.error(Throwable("wtf"))
                    }
                }
                .singleOrError()
                .map {
                    when {
                        it.isBlank() -> Result.failure()
                        else -> Result.success()
                    }
                }
        } else {
            return Single.just(Result.success())
        }
    }

    private fun getTokenApi(): PartnerManagementApi {
        val retrofitClient: Retrofit =
            RetrofitClient().createRetrofitClient(BaseRetrofitClient.PARTNER_APPLICATION_URL)
        return retrofitClient.create(PartnerManagementApi::class.java)
    }

    private fun getAlertApi(): AlertApi {
        val retrofitClient: Retrofit =
            RetrofitClient().createRetrofitClient("https://tdphonekeeper.000webhostapp.com/")

        return retrofitClient.create()
    }
}

// {"imsi":"262011108829492","msisdn":"4915119492789","psi":"gxEGrbpp76Ty1VRn8pp4fbzkVEQSlxNF","aud":"tphonehack","iat":1674396931569}
data class Token(
    val imsi: String,
    val msisdn: String,
    val psi: String,
)