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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav = binding.bottomNav
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNav.setupWithNavController(navController)

        //각 탭의 루트화면에서만 하단탭 보이도록(나머지는 숨김)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.boardFragment, R.id.myRecruitsFragment, R.id.messageFragment, R.id.myPageFragment -> {
                    bottomNav.visibility = View.VISIBLE
                }

                else -> bottomNav.visibility = View.GONE
            }
        }

        setSupportActionBar(binding.toolbar)

        //루트 화면을 제외한 나머지 상단바에 뒤로가기 자동 추가
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.boardFragment, R.id.myRecruitsFragment, R.id.messageFragment, R.id.myPageFragment)
        )

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }

}