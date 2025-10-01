package com.baek.untitledproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val sessionViewModel: SessionViewModel by viewModels()

    private val rootDestinations = setOf(
        R.id.boardFragment, R.id.myRecruitsFragment, R.id.messageFragment
    )

    private val navController by lazy {
        val host =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        host.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //viewModel 실행을 위해
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                sessionViewModel.session.collect { }
            }
        }

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
        binding.xToolbar.setNavigationOnClickListener {
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
        xToolbarVisible: Boolean = false,
        title: String? = null
    ) {
        binding.rootToolbar.visibility = if (rootVisible) View.VISIBLE else View.GONE
        binding.detailToolbar.visibility = if (detailVisible) View.VISIBLE else View.GONE
        binding.xToolbar.visibility = if (xToolbarVisible) View.VISIBLE else View.GONE
        title?.let {
            when {
                rootVisible -> binding.rootToolbar.title = it
                detailVisible -> binding.detailToolbar.title = it
                xToolbarVisible -> binding.xToolbar.title = it
            }
        }
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navController.handleDeepLink(intent)
    }


}