package com.operatortokenocb

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.work.*
import com.operatortokenocb.data.*
import com.operatortokenocb.databinding.FormRegisterBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FormRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sp = getSharedPreferences("fuck", Context.MODE_PRIVATE)

        ContactRepository(sp).getInfo()?.let {
            binding.editTextTextEmailAddress2.setText(it.email)
            binding.editTextTextPersonFirst.setText(it.firstName)
            binding.editTextTextPersonSecond.setText(it.lastName)
        }

//
//        binding.buttonTriggerSimInject.setOnClickListener {
//            Timber.tag("MA").d("button clicked")
//
//            val workRequest = OneTimeWorkRequestBuilder<TokenCheckingWork>()
//                .setConstraints(
//                    Constraints.Builder()
//                        .setRequiredNetworkType(NetworkType.METERED)
//                        .build()
//                )
//                .build()
//
//            val operation = WorkManager.getInstance(this)
//                .enqueueUniqueWork(TokenCheckingWork.WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
//            operation.state.observe(this) {
//                Timber.tag("MA").d(it.toString())
//            }
//        }

        binding.button2.setOnClickListener {
            val email = binding.editTextTextEmailAddress2.text.toString()
            val first = binding.editTextTextPersonFirst.text.toString()
            val last = binding.editTextTextPersonSecond.text.toString()

            if (email.isNotBlank()) {
                ContactRepository(getSharedPreferences("fuck", Context.MODE_PRIVATE))
                    .storeInfo(
                        Info(
                            firstName = first,
                            lastName = last,
                            email = email,
                        )
                    )
            }
        }


        // TODO check the current safe/not safe
        val workRequest = OneTimeWorkRequestBuilder<TokenCheckingWork>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.METERED)
                    .build()
            )
            .build()

        val operation = WorkManager.getInstance(this)
            .enqueueUniqueWork(TokenCheckingWork.WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
        operation.state.observe(this) {
            Timber.tag("MA").d(it.toString())

            sp.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
                when (key) {
                    HackRepository.KEY -> {
                        val k = sharedPreferences.getInt(HackRepository.KEY, -1)
                        when (k.toStatus()) {
                            Status.Loading -> {
                                binding.progressBar.isVisible = true
                                binding.keyLogo.isVisible = false
                            }
                            Status.Same -> {
                                binding.progressBar.isVisible = false
                                binding.keyLogo.run {
                                    isVisible = true
                                    setImageResource(R.drawable.green_key)
                                }
                            }
                            Status.Different -> {
                                binding.progressBar.isVisible = true
                                binding.keyLogo.run {
                                    isVisible = true
                                    setImageResource(R.drawable.red_key)
                                }
                            }
                            null -> TODO()
                        }
                    }
                }
            }
        }
    }
}