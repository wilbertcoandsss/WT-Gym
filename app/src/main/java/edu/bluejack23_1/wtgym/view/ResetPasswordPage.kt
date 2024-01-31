package edu.bluejack23_1.wtgym.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.ActivityForgotPaswordPageBinding
import edu.bluejack23_1.wtgym.databinding.ActivityResetPasswordPageBinding
import edu.bluejack23_1.wtgym.viewmodel.ForgotPasswordViewModel
import edu.bluejack23_1.wtgym.viewmodel.ResetPasswordViewModel

class ResetPasswordPage : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordPageBinding
    private lateinit var viewModel: ResetPasswordViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password_page)

        Log.d("coba intent", intent.getStringExtra("email").toString())
        // Retrieve the email value from the Intent
        val email = intent.getStringExtra("email")

        // Find the TextView by its ID
        val textView = findViewById<TextView>(R.id.textView9)

        // Set the text of the TextView
        textView.text = email

        binding = DataBindingUtil.setContentView(this, R.layout.activity_reset_password_page)
        viewModel = ViewModelProvider(this).get(ResetPasswordViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.currentEmail.value = intent.getStringExtra("email").toString()

        viewModel.passwordError.observe(this, Observer { pwError ->
            if (pwError != null) {
                binding.lblError.text = pwError
                binding.inputNewPasswordLbl.error = pwError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.inputNewPasswordLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.inputNewPasswordLbl.setBackgroundResource(R.drawable.edittext_fails)
            } else {
                binding.inputNewPasswordLbl.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.confirm_passwordError.observe(this, Observer { confirmPwError ->
            if (confirmPwError != null) {
                binding.lblError.text = confirmPwError
                binding.inputConfirmPasswordLbl.error = confirmPwError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.inputConfirmPasswordLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.inputConfirmPasswordLbl.setBackgroundResource(R.drawable.edittext_fails)

            } else {
                binding.inputConfirmPasswordLbl.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.navigateToLoginPage.observe(this, Observer { navigate ->
            if (navigate){
                val message = "Password succesfully reset!"

                // Use the root layout of your activity as the view for the Snackbar
                val rootView = findViewById<View>(android.R.id.content)

                // Create a Snackbar
                val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
                snackbar.setBackgroundTint(Color.parseColor("#3A3B3C"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                // Add an action to the Snackbar (optional)
                snackbar.setAction("Dismiss") {
                    // Handle the action click event here
                }
                // Show the Snackbar
                snackbar.show()

                Handler().postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }, 2500) // 3000 milliseconds (3 seconds)
            }
        })

        binding.resetPwBtn.setOnClickListener{
            viewModel.onResetPwClicked()
        }
    }
}