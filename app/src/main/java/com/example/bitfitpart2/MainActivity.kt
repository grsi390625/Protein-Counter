package com.example.bitfitpart2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavigationView: BottomNavigationView
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (firebaseAuth.currentUser == null) {
            redirectToAuth()
        }
    }

    companion object {
        private const val KEY_SELECTED_TAB = "selected_tab"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            redirectToAuth()
            return
        }
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        val addButton: Button = findViewById(R.id.addButton)
        val signOutButton: Button = findViewById(R.id.signOutButton)

        val entryFragment = EntryFragment()
        val navigationFragment = NavigationFragment()
        val reportFragment = ReportFragment()

        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.entries -> entryFragment
                R.id.navigation -> navigationFragment
                R.id.report -> reportFragment
                else -> entryFragment
            }
            supportFragmentManager.beginTransaction().replace(R.id.logs_frame_layout, selectedFragment).commit()
            true
        }

        // BottomNavigationView restores its own checked item on recreation without
        // re-notifying the listener, which would leave the visible tab and the loaded
        // fragment out of sync; tracking the selection ourselves keeps them consistent.
        val restoredTab = savedInstanceState?.getInt(KEY_SELECTED_TAB) ?: R.id.entries
        bottomNavigationView.selectedItemId = restoredTab

        // Add button click listener
        addButton.setOnClickListener {
            val intent = Intent(this, AddEntryActivity::class.java)
            startActivity(intent)
        }

        signOutButton.setOnClickListener {
            auth.signOut()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::bottomNavigationView.isInitialized) {
            outState.putInt(KEY_SELECTED_TAB, bottomNavigationView.selectedItemId)
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authStateListener)
    }

    private fun redirectToAuth() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
