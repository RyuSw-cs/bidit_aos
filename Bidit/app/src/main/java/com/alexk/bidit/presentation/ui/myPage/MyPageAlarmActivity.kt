package com.alexk.bidit.presentation.ui.myPage

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.alexk.bidit.R
import com.alexk.bidit.data.sharedPreference.TokenManager
import com.alexk.bidit.databinding.ActivityMyPageAlarmBinding
import com.alexk.bidit.di.ViewState
import com.alexk.bidit.dialog.LoadingDialog
import com.alexk.bidit.presentation.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class MyPageAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyPageAlarmBinding
    private val userViewModel by viewModels<UserViewModel>()
    private val loadingDialog by lazy { LoadingDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page_alarm)
        setContentView(binding.root)

        init()
        initEvent()
    }

    private fun init() {
        observePushToken()
        userViewModel.getMyInfo()
    }

    private fun initEvent() {
        binding.apply {
            cbAllPushAlarm.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    userViewModel.updatePushToken(0,TokenManager(this@MyPageAlarmActivity).getPushToken())
                }
                else{
                    userViewModel.updatePushToken(1,TokenManager(this@MyPageAlarmActivity).getPushToken())
                }
            }
        }
    }

    private fun observePushToken() {
        userViewModel.myInfo.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    loadingDialog.show()
                    Log.d("PushToken", "Loading")
                }
                is ViewState.Success -> {
                    loadingDialog.dismiss()
                    Log.d("PushToken", "Success")
                    val result = response.value?.data?.me?.pushToken?.status
                    binding.cbAllPushAlarm.isChecked = result == 0
                }
                is ViewState.Error -> {
                    loadingDialog.dismiss()
                    Log.d("PushToken", "Error")
                }
            }
        }

        userViewModel.pushToken.observe(this) { response ->
            when (response) {
                is ViewState.Loading -> {
                    loadingDialog.show()
                    Log.d("PushToken", "Loading")
                }
                is ViewState.Success -> {
                    loadingDialog.dismiss()
                    Log.d("PushToken", "Success")
                    userViewModel.getMyInfo()
                }
                is ViewState.Error -> {
                    loadingDialog.dismiss()
                    Log.d("PushToken", "Error")
                }
            }
        }
    }
}