package com.baek.untitledproject.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.baek.untitledproject.R
import com.baek.untitledproject.databinding.ViewImageSliderBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ImageSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private val binding = ViewImageSliderBinding.inflate(LayoutInflater.from(context), this, true)

    private val adapter = ImageSliderAdapter()

    init {
        orientation = VERTICAL
        binding.viewPager.adapter = adapter
        initSelectedDotListener()
    }

    fun submitList(imageList: List<String>) {
        //이미지 리스트를 어댑터에 전달
        adapter.submitList(imageList)

        //ViewPager와 TabLayout 연결 및 커스텀 뷰(item_tab_dot) 설정
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, _ ->
            tab.setCustomView(R.layout.item_tab_dot)
        }.attach()

        //첫번째 dot을 선택된 상태(tab_dot_selected)로 변경
        binding.tabLayout.post {
            val firstDot =
                binding.tabLayout.getTabAt(0)?.customView?.findViewById<ImageView>(R.id.dot)
            firstDot?.setImageResource(R.drawable.tab_dot_selected)
        }
    }

    private fun initSelectedDotListener() {

        //이미지 변경에 따른 dot 선택 상태 변경
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val dot = tab?.customView?.findViewById<ImageView>(R.id.dot)
                dot?.setImageResource(R.drawable.tab_dot_selected)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val dot = tab?.customView?.findViewById<ImageView>(R.id.dot)
                dot?.setImageResource(R.drawable.tab_dot)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

    }
}
