package edu.bluejack23_1.wtgym.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import edu.bluejack23_1.wtgym.model.UserRepository

class ResetPasswordViewModel : ViewModel(){

    val _navigateToLoginPage = MutableLiveData<Boolean>()
    val navigateToLoginPage: LiveData<Boolean>
        get() = _navigateToLoginPage

    val password = MutableLiveData<String>()
    val confirm_password = MutableLiveData<String>()
    val passwordError = MutableLiveData<String?>()
    val confirm_passwordError = MutableLiveData<String?>()

    var currentEmail = MutableLiveData<String>()

    fun onResetPwClicked(){

        val auth = FirebaseAuth.getInstance()

        val passwordValue = password.value
        val confirmPwValue = confirm_password.value

        confirm_passwordError.value = null
        passwordError.value = null

        if (passwordValue.isNullOrBlank()){
            passwordError.value = "New password is required!"
            return
        }

        if (!passwordValue!!.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$"))) {
            passwordError.value = "New password must be alphanumeric."
            return
        }

        if (confirmPwValue.isNullOrBlank()){
            confirm_passwordError.value = "Confirm your new password!"
            return
        }

        if (confirmPwValue != passwordValue){
            confirm_passwordError.value = "Confirm password must be the same!"
            return
        }

        if (confirmPwValue.isNotBlank() && passwordValue.isNotBlank()){
            val userRepository = UserRepository(null)
            userRepository.resetPassword(this)
        }
    }
}