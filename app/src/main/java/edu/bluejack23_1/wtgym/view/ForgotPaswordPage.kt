package edu.bluejack23_1.wtgym.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.ActivityForgotPaswordPageBinding
import edu.bluejack23_1.wtgym.viewmodel.ForgotPasswordViewModel
import edu.bluejack23_1.wtgym.viewmodel.LoginViewModel

class ForgotPaswordPage : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPaswordPageBinding
    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_pasword_page)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_pasword_page)
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        println("TESTING")

        viewModel.emailError.observe(this, Observer { emailError ->
            if (emailError != null){
                binding.lblError.text = emailError;
                binding.inputLblEmail.error = emailError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.inputLblEmail.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                binding.inputLblEmail.setBackgroundResource(R.drawable.edittext_fails)
            }
            else{
                binding.inputLblEmail.setBackgroundResource(R.drawable.edittext_roundedcorner)
            }
        })

        viewModel.navigateToResetPassword.observe(this, Observer { navigate ->
            if (navigate){
                val intent = Intent(this, ResetPasswordPage::class.java)
                intent.putExtra("email", viewModel.email.value.toString())
                startActivity(intent)
            }
            else{

            }
        })

        binding.findEmailBtn.setOnClickListener {
            viewModel.onFindEmailClicked()
        }

    }
}