package edu.bluejack23_1.wtgym.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.wtgym.view.RegisterActivity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class RegisterViewModel : ViewModel(){

    private val _navigateToLogin = MutableLiveData<Boolean>()

    val navigateToLogin: LiveData<Boolean>
        get() = _navigateToLogin

    val name = MutableLiveData<String>()
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val weight = MutableLiveData<String>()
    val height = MutableLiveData<String>()
    val dob = MutableLiveData<String>()

    val nameError = MutableLiveData<String?>()
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val weightError = MutableLiveData<String?>()
    val heightError = MutableLiveData<String?>()
    val dobError = MutableLiveData<String?>()

    private lateinit var activity: RegisterActivity

    fun setActivityReference(activity: RegisterActivity) {
        this.activity = activity
    }

    fun onDatePickerClicked(){
        activity.datePickerDialog.show()
    }

    fun validateAge(dobValue: String): Boolean {
        val dobFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dobDate = LocalDate.parse(dobValue, dobFormatter)
        val currentDate = LocalDate.now()
        val ageLimitDate = currentDate.minusYears(16)

        return dobDate.isAfter(ageLimitDate)
    }

    fun formatDateString(dobValue: String): String {
        val parts = dobValue.split(" ")
        val month = parts[0].toLowerCase().capitalize()
        return "$month ${parts[1]} ${parts[2]}"
    }

    fun convertToNewDateFormat(dobValue: String): String {
        val oldFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH)
        val newFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val date = LocalDate.parse(dobValue, oldFormatter)
        return date.format(newFormatter)
    }

    fun onRegisterClicked(){
        val nameValue = name.value
        val emailValue = email.value
        val passwordValue = password.value
        val weightValue = weight.value?.toIntOrNull()
        val heightValue = height.value?.toIntOrNull()
        val dobValue = dob.value

        nameError.value = null
        emailError.value = null
        passwordError.value = null
        weightError.value = null
        heightError.value = null
        dobError.value = null

        if (nameValue.isNullOrBlank()) {
            nameError.value = "Name is required!"
            return
        }

        if (emailValue.isNullOrBlank()) {
            emailError.value = "Email is required!"
            return
        }

        var isEmailError = false
        // Check if the email exists in the Firestore collection
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("email", emailValue)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Email is not in the Firestore collection, proceed with user registration
                    // Your registration logic here
                    Log.d("EMAIL_NOT_FOUND", "Email is not in Firestore collection")
                } else {
                    // Email is already in the Firestore collection, display an error message
                    isEmailError = true
                    Log.d("EMAIL_FOUND", "Email is already in Firestore collection")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_ERROR", "Error fetching Firestore documents: $e")
                // Handle the error as needed
            }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            emailError.value = "Invalid email address"
            return
        }

        if (passwordValue.isNullOrBlank()) {
            passwordError.value = "Password is required!"
            return
        }

        if (!passwordValue!!.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d).+\$"))) {
            passwordError.value = "Password must be alphanumeric!"
            return
        }

        if (weightValue == null || weightValue <= 0) {
            weightError.value = "Weight must be greater than 0!"
            return
        }

        if (heightValue == null || heightValue <= 0) {
            heightError.value = "Height must be greater than 0!"
            return
        }

        if (dobValue.isNullOrBlank()) {
            dobError.value = "DOB is required!"
            return
        }

        val formattedDobValue = formatDateString(dobValue.toString())
        val convertedDobDate = convertToNewDateFormat(formattedDobValue.toString())
        val isAgeValid = validateAge(convertedDobDate.toString())
        if (!isAgeValid) {
            println("Valid age!")
        } else {
            dobError.value = "Age must be more than 15 years!"
            return
        }

        if(isEmailError){
            emailError.value = "Email must be unique!"
            return
        }

        nameError.value = null
        emailError.value = null
        passwordError.value = null
        weightError.value = null
        heightError.value = null
        dobError.value = null

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailValue, passwordValue)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign up success, update UI with the signed-in user's information
                    val userUID = FirebaseAuth.getInstance().currentUser?.uid

                    // store it into firestore users
                    userUID?.let {
                        val user = hashMapOf(
                            "email" to emailValue,
                            "password" to passwordValue,
                            "weight" to weightValue,
                            "height" to heightValue,
                            "name" to nameValue,
                            "role" to "user",
                            "dob" to dobValue
                            // Add other user information here
                        )

                        FirebaseFirestore.getInstance().collection("users")
                            .document(userUID)
                            .set(user)
                            .addOnSuccessListener {

                                Log.d("Firestore", "DocumentSnapshot successfully written!")
                                // You can perform further actions here after successfully storing the user's information
                            }
                            .addOnFailureListener { e ->
                                Log.w("Firestore", "Error writing document", e)
                                // Handle any errors that may occur during the document writing process
                            }
                    }

                    nameError.value = null
                    emailError.value = null
                    passwordError.value = null
                    weightError.value = null
                    heightError.value = null
                    dobError.value = null
                    _navigateToLogin.value = true;

                    Log.w("BERHASIL", "BISA GOBLOK", task.exception)
                    // You can add further actions here after successful registration
                } else {
                    // If sign in fails, display a message to the user.
                    emailError.value = "Email must be unique!"
                    Log.w("GAGAL", "createUserWithEmail:failure", task.exception)
                    // Show an error message or toast
                }
            }


    }

}