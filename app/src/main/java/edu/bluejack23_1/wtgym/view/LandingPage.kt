package edu.bluejack23_1.wtgym.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.wtgym.MainActivity
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.ActivityLandingPageBinding
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.PageNavigationViewModel

class LandingPage : AppCompatActivity() {

    private lateinit var binding: ActivityLandingPageBinding
    private lateinit var viewModel: PageNavigationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_landing_page)
        viewModel = ViewModelProvider(this).get(PageNavigationViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Retrieve values from SharedPreferences
        val sharedPreferences: SharedPreferences? =
            this?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.clear()
        editor?.apply()

//        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val userRepo = UserRepository(this)
        Log.d("Ini Landing Page", userRepo.getCurrentUser()?.userName.toString())


        val goToLoginBtn = findViewById<Button>(R.id.goToLoginPage)
        val goToRegisterBtn = findViewById<Button>(R.id.goToRegisterPage)

        // Observe the navigation event
        viewModel.navigateToLogin.observe(this, Observer {
            if (it) {
                // Navigate to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
                // Reset the navigation flag
                viewModel.onNavigateToLoginComplete()
            }
        })

        viewModel.navigateToRegister.observe(this, Observer{
            if (it) {
                startActivity(Intent(this, RegisterActivity::class.java))
                viewModel.onNavigateToRegisterComplete()
            }
        })
    }
}