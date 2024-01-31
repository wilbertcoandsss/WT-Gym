package edu.bluejack23_1.wtgym.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import edu.bluejack23_1.wtgym.MainActivity
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.ActivityLoginBinding
import edu.bluejack23_1.wtgym.viewmodel.LoginViewModel
import edu.bluejack23_1.wtgym.viewmodel.PageNavigationViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.loginBtn.setOnClickListener {
            viewModel.onLoginClicked()
        }

        binding.forgotPwLink.setOnClickListener {
            val intent = Intent(this, ForgotPaswordPage::class.java)
            startActivity(intent)
        }

        // Observe error messages and update UI accordingly
        viewModel.emailError.observe(this, Observer { emailError ->
            if (emailError != null) {
                binding.lblError.text = emailError;
                binding.inputLblEmail.error = emailError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.inputLblEmail.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.inputLblEmail.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.inputLblEmail.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.passwordError.observe(this, Observer { passwordError ->
            if (passwordError != null) {
                binding.lblError.text = passwordError
                binding.passwordInputLbl.error = passwordError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.passwordInputLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.passwordInputLbl.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.passwordInputLbl.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
            // Update the UI to display the password error message and change the EditText outline color to red

            // You can customize the error appearance as needed
        })

        viewModel.navigateToMain.observe(this, Observer { navigate ->
            if (navigate) {
                sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("email", viewModel.email.value.toString())
                startActivity(intent)
            } else {

            }
        })
    }
}