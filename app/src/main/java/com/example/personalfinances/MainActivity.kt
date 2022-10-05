package com.example.personalfinances

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.personalfinances.accounts.AccountsFragment
import com.example.personalfinances.categories.CategoriesFragment
import com.example.personalfinances.data.MainDb
import com.example.personalfinances.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private val accDb by lazy { MainDb.getDb(this).accDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        observeCatDb()
        openFragment(R.id.fragment_container, CategoriesFragment())
    }


    /*
    This function is used to observe changes in our database. Whenever data is changed, the code in
    the curly braces us run. In our case this updates the content in our adapter.
     */
    // TODO: Add dependency injection (Dagger) to avoid database initialization everytime
    private fun observeCatDb() {
        lifecycleScope.launch {
            accDb.getAll().collect {
                // Set top bar data
                val balance = accDb.sumBalance()
                if (balance != null) binding.toolBar.title = "$${balance}"
                else binding.toolBar.title = "$0"

            }
        }
    }

    // This function is used to initialize views and their inner content
    private fun init() {
        binding.apply {
            // Set selected item to be CategoriesFragment
            botNav.selectedItemId = R.id.bot_nav_categories
        }

        // Set Listeners for bottom navigation
        binding.botNav.setOnItemSelectedListener { item ->
            when (item.itemId){
                R.id.bot_nav_categories -> {
                    openFragment(R.id.fragment_container, CategoriesFragment())
                    true
                }
                R.id.bot_nav_accounts -> {
                    openFragment(R.id.fragment_container, AccountsFragment())
                    true
                }
                R.id.bot_nav_transactions -> {
                    // TODO: add Transactions Fragment
                    openFragment(R.id.fragment_container, CategoriesFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(placeHolder: Int, frag: Fragment) {
        // This code replaces the data in our FragmentView with an instance of our Fragment Class
        supportFragmentManager
            .beginTransaction()
            .replace(placeHolder, frag)
            .commit()
    }
}