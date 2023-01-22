package com.operatortokenocb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.operatortokenocb.TokenCheckingWork.Companion.WORK_NAME
import timber.log.Timber

class SimChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (val intentAction = intent?.action) {
            "android.intent.action.SIM_STATE_CHANGED" -> checkWithExistingToken(context ?: error("wtf"))
            else -> error("$intentAction is not expected.")
        }
    }

    private fun checkWithExistingToken(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<TokenCheckingWork>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.METERED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)

        Timber.tag("SCR").d("work scheduled")
    }
}