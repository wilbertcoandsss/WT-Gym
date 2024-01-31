package edu.bluejack23_1.wtgym.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.wtgym.model.UserRepository

class ForgotPasswordViewModel : ViewModel(){
    val _navigateToResetPassword = MutableLiveData<Boolean>()
    val navigateToResetPassword: LiveData<Boolean>
        get() = _navigateToResetPassword

    val email = MutableLiveData<String>()
    val emailError = MutableLiveData<String>()

    fun onFindEmailClicked(){
        val emailValue = email.value

        emailError.value = null

        if (emailValue.isNullOrBlank()){
            emailError.value = "Email is required!"
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            emailError.value = "Invalid email address"
            return
        }

        if (emailValue.isNotBlank()) {
            val userRepository = UserRepository(null)
            userRepository.findEmail(this)
        }
    }
}