package com.baek.untitledproject.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val rootDestinations = setOf(
        R.id.boardFragment, R.id.myRecruitsFragment, R.id.messageFragment, R.id.myPageFragment
    )

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
            navController.popBackStack()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isRoot = destination.id in rootDestinations

            binding.bottomNav.visibility = if (isRoot) View.VISIBLE else View.GONE

            binding.rootToolbar.visibility = if (isRoot) View.VISIBLE else View.GONE
            binding.detailToolbar.visibility = if (isRoot) View.GONE else View.VISIBLE

        }
    }

    fun setRootTitle(title:String){
        binding.rootToolbar.title = title
    }
    fun setDetailTitle(title: String) {
        binding.detailToolbar.title = title
    }

}