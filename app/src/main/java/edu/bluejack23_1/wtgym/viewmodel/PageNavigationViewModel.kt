package edu.bluejack23_1.wtgym.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PageNavigationViewModel: ViewModel() {
    private val _navigateToLogin = MutableLiveData<Boolean>()
    private val _navigateToRegister = MutableLiveData<Boolean>()
    private val _navigateToForgotPassword = MutableLiveData<Boolean>()


    val navigateToLogin: LiveData<Boolean>
        get() = _navigateToLogin

    val navigateToRegister: LiveData<Boolean>
        get() = _navigateToRegister

    val navigateToForgotPassword: LiveData<Boolean>
        get() = _navigateToForgotPassword

    fun onGoToLoginClick(view: View) {
        _navigateToLogin.value = true
    }

    fun onNavigateToLoginComplete() {
        _navigateToLogin.value = false
    }

    fun onGoToRegisterClick(view: View){
        _navigateToRegister.value = true
    }

    fun onNavigateToRegisterComplete(){
        _navigateToRegister.value = false;
    }

    fun onGoToForgotPasswordClick(view: View){
        _navigateToForgotPassword.value = true
    }

    fun onNavigateToForgotPasswordComplete(){
        _navigateToForgotPassword.value = false
    }
}