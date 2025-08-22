package com.baek.untitledproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ActivityMainBinding
import com.baek.untitledproject.ui.login.EmailVerifyFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val rootDestinations = setOf(
        R.id.boardFragment, R.id.myRecruitsFragment, R.id.messageFragment, R.id.myPageFragment
    )

    private val navController by lazy {
        val host =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        host.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav = binding.bottomNav
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNav.setupWithNavController(navController)

        //toolbar/bottomNav 설정

        setSupportActionBar(binding.rootToolbar)
        val appBarConfiguration = AppBarConfiguration(rootDestinations)
        binding.rootToolbar.setupWithNavController(navController, appBarConfiguration)

        binding.detailToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isRoot = destination.id in rootDestinations

            binding.bottomNav.visibility = if (isRoot) View.VISIBLE else View.GONE

            binding.rootToolbar.visibility = if (isRoot) View.VISIBLE else View.GONE
        }
        navController.handleDeepLink(intent)
    }

    //그냥 호출 시 toolbar 사라지도록
    fun setToolbar(
        rootVisible: Boolean = false,
        detailVisible: Boolean = false,
        title: String? = null
    ) {
        binding.rootToolbar.visibility = if (rootVisible) View.VISIBLE else View.GONE
        binding.detailToolbar.visibility = if (detailVisible) View.VISIBLE else View.GONE
        title?.let {
            when {
                rootVisible -> binding.rootToolbar.title = it
                detailVisible -> binding.detailToolbar.title = it
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController.handleDeepLink(intent)
    }


}