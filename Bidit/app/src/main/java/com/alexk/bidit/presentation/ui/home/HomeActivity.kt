package com.alexk.bidit.presentation.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.navigation.ui.setupWithNavController
import com.alexk.bidit.R
import com.alexk.bidit.databinding.ActivityHomeBinding
import com.alexk.bidit.presentation.base.BaseActivity
import com.alexk.bidit.presentation.ui.search.SearchActivity

class HomeActivity :
    BaseActivity<ActivityHomeBinding>(R.layout.activity_home, R.id.nav_home_fragment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        initEvent()
    }

    override fun onBackPressed() {
        val f = getCurrentFragment()
        if (f is HomeFragment) {
            finishAffinity()
            return
        }

        if (!navController.navigateUp()) {
            super.onBackPressed()
        }
    }

    override fun init() {
        binding.apply {
            navHomeBottom.setupWithNavController(navController)
        }
    }

    override fun initEvent() {

    }
}