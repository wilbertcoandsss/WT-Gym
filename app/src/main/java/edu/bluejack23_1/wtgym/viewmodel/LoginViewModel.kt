package edu.bluejack23_1.wtgym.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.wtgym.model.UserRepository


class LoginViewModel() : ViewModel() {
    val _navigateToMain = MutableLiveData<Boolean>()
    val navigateToMain: LiveData<Boolean>
        get() = _navigateToMain

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()

    fun onLoginClicked() {
        val emailValue = email.value
        val passwordValue = password.value

        // Reset previous error messages
        emailError.value = null
        passwordError.value = null

        if (emailValue.isNullOrBlank()) {
            emailError.value = "Email is required!"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            emailError.value = "Invalid email address"
            return
        }

        if (passwordValue.isNullOrBlank()) {
            passwordError.value = "Password is required"
            return
        }

        if (emailValue.isNotBlank() && passwordValue.isNotBlank()) {
            emailError.value = null
            passwordError.value = null

            val userRepository = UserRepository(null)
            userRepository.logIn(this)
        }

        // Validation successful; perform the login action
        // You can call your authentication logic here
    }
}
