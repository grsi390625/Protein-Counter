package com.example.bitfitpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val addButton: Button = findViewById(R.id.addButton)

        val entryFragment = EntryFragment()
        val navigationFragment = NavigationFragment()

        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.entries -> entryFragment
                R.id.navigation -> navigationFragment
                else -> entryFragment
            }
            supportFragmentManager.beginTransaction().replace(R.id.logs_frame_layout, selectedFragment).commit()
            true
        }

        // Default selected fragment
        bottomNavigationView.selectedItemId = R.id.entries

        // Add button click listener
        addButton.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivity(intent)
        }
    }
}
