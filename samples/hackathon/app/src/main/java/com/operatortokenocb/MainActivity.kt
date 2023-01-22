package com.operatortokenocb

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.operatortokenocb.data.ContactRepository
import com.operatortokenocb.data.Info
import com.operatortokenocb.databinding.FormRegisterBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FormRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonTriggerSimInject.setOnClickListener {
            Timber.tag("MA").d("button clicked")

            val workRequest = OneTimeWorkRequestBuilder<TokenCheckingWork>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.METERED)
                        .build()
                )
                .build()

            val operation = WorkManager.getInstance(this).enqueue(workRequest)
            operation.state.observe(this) {
                Timber.tag("MA").d(it.toString())
            }
        }

        binding.button2.setOnClickListener {
            val email = binding.editTextTextEmailAddress2.text.toString()
            val first = binding.editTextTextPersonFirst.text.toString()
            val last = binding.editTextTextPersonSecond.text.toString()

            if (email.isNotBlank()) {
                ContactRepository(getSharedPreferences("fuck", Context.MODE_PRIVATE))
                    .storeInfo(
                        Info(
                            firstName = "TODO",
                            lastName = "TODO",
                            email = email,
                        )
                    )
            }


        }
    }
}