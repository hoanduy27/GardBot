package com.example.gardbot.ui.manager

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.gardbot.ui.manager.SectionsPagerAdapter
import com.example.gardbot.databinding.ActivityOperatorManagerBinding

class OperatorManagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOperatorManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOperatorManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabsManager
        tabs.setupWithViewPager(viewPager)
    }
}