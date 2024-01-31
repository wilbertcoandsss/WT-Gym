package edu.bluejack23_1.wtgym.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edu.bluejack23_1.wtgym.model.UserRepository

class AccountViewModel() : ViewModel() {
    val _backToSetting = MutableLiveData<Boolean>()
    val backToSetting: LiveData<Boolean>
        get() = _backToSetting

    val nameAcc = MutableLiveData<String>()
    val weightAcc = MutableLiveData<Int>()
    val heightAcc = MutableLiveData<Int>()

    val nameError = MutableLiveData<String?>()
    val weightError = MutableLiveData<String?>()
    val heightError = MutableLiveData<String?>()


    val password = MutableLiveData<String>()
    val newPassword = MutableLiveData<String>()
    val confirmPassword = MutableLiveData<String>()
    val pwError = MutableLiveData<String?>()

    fun onChangeNameClicked(userId: String, userName: String, context: Context): Boolean{
        val nameValue = nameAcc.value

        nameError.value = null

        if (nameValue.isNullOrBlank()){
            nameError.value = "Name must be filled!"
            return false
        }

        if (!nameValue.matches(Regex("^[a-zA-Z ]+\$"))) {
            nameError.value = "Name must contain only alphabetic characters"
            return false
        }

        if (nameValue == userName){
            nameError.value = "Name must be different!"
            return false
        }

        val userRepository = UserRepository(null)

        if (nameValue.isNotBlank()){
            userRepository.changeName(userId, nameValue, context);
            return true
        }
        return false
    }

    fun onChangeHeightClicked(userId: String, userHeight: Int, context: Context): Boolean{
        val heightValue = heightAcc.value

        heightError.value = null

        if (heightValue == null){
            heightError.value = "Height must be filled!"
            return false
        }

        if (heightValue == 0){
            heightError.value = "Height must be not 0!"
            return false
        }

        if (!heightValue.toString().matches(Regex("^\\d+$"))) {
            heightError.value = "Height must contain only numeric"
            return false
        }

        if (heightValue == userHeight){
            heightError.value = "Height must be different!"
            return false
        }

        val userRepository = UserRepository(null)

        if (heightValue != 0 || heightValue != null){
            userRepository.changeHeight(userId, heightValue, context);
            return true
        }
        return false
    }

    fun onChangeWeightClicked(userId: String, userWeight: Int, context: Context): Boolean{
        val weightValue = weightAcc.value

        weightError.value = null

        if (weightValue == null){
            weightError.value = "Weight must be filled!"
            return false
        }

        if (weightValue == 0){
            weightError.value = "Weight must be not 0!"
            return false
        }

        if (!weightValue.toString().matches(Regex("^\\d+$"))) {
            weightError.value = "Weight must contain only numeric"
            return false
        }

        if (weightValue == userWeight){
            weightError.value = "Weight must be different!"
            return false
        }

        val userRepository = UserRepository(null)

        if (weightValue != 0 || weightValue != null){
            userRepository.changeWeight(userId, weightValue, context);
            return true
        }
        return false
    }

    fun onChangePwClicked(userId: String, context : Context): Boolean{
        val oldPwValue = password.value
        val newPwValue = newPassword.value
        val confirmPwValue = confirmPassword.value

        pwError.value = null

        if (oldPwValue.isNullOrBlank()){
            pwError.value = "Current Password must be filled!"
            return false
        }

        if (newPwValue.isNullOrBlank()){
            pwError.value = "New Password must be filled!"
            return false
        }

        if (confirmPwValue.isNullOrBlank()){
            pwError.value = "Confirm Password must be filled!"
            return false
        }

        val userRepository = UserRepository(context)
        val userPw = userRepository.getCurrentUser()?.userPassword

        if (oldPwValue != userPw){
            pwError.value = "Current password is not match!"
            return false
        }

        if (!newPwValue.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$"))) {
            pwError.value = "New Password must be alphanumeric!"
            return false
        }

        if (newPwValue != confirmPwValue){
            pwError.value = "Confirm password is not match!"
            return false
        }

        if (oldPwValue.isNotBlank() && newPwValue.isNotBlank() && confirmPwValue.isNotBlank()){
            userRepository.changePassword(userId, confirmPwValue, context)
            return true
        }

        return false
    }
}