package edu.bluejack23_1.wtgym.model

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.wtgym.viewmodel.ForgotPasswordViewModel
import edu.bluejack23_1.wtgym.viewmodel.LoginViewModel
import edu.bluejack23_1.wtgym.viewmodel.ResetPasswordViewModel

data class User(
    val userId: String,
    val userName: String,
    val userEmail: String,
    val userPassword: String,
    val userRole: String,
    val userHeight: Int,
    val userWeight: Int,
    val userDob: String,
    val userProfilePic: String,
)

class UserRepository(private val context: Context?) {
    private val sharedPreferences: SharedPreferences? =
        context?.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun setCurrentUser(userModel: User) {
        sharedPreferences?.edit()?.clear()?.apply()
        val editor = sharedPreferences?.edit()
        editor?.putString("userId", userModel.userId)
        editor?.putString("userName", userModel.userName)
        editor?.putString("userEmail", userModel.userEmail)
        editor?.putString("userPassword", userModel.userPassword)
        editor?.putString("userRole", userModel.userRole)
        editor?.putInt("userHeight", userModel.userHeight)
        editor?.putInt("userWeight", userModel.userWeight)
        editor?.putString("userRole", userModel.userRole)
        editor?.putString("userDob", userModel.userDob)
        editor?.putString("userProfilePic", userModel.userProfilePic)
        editor?.apply()
    }

    fun getCurrentUser(): User? {
        val userId = sharedPreferences?.getString("userId", null)
        val userName = sharedPreferences?.getString("userName", null)
        val userEmail = sharedPreferences?.getString("userEmail", null)
        val userPassword = sharedPreferences?.getString("userPassword", null)
        val userRole = sharedPreferences?.getString("userRole", null)
        val userHeight =
            sharedPreferences?.getInt("userHeight", 0) // Replace 0 with a default value
        val userWeight =
            sharedPreferences?.getInt("userWeight", 0) // Replace 0 with a default value
        val userDob = sharedPreferences?.getString("userDob", null)
        val userProfilePic = sharedPreferences?.getString("userProfilePic", null)

        if (userId != null && userName != null && userRole != null && userEmail != null && userPassword != null && userHeight != null && userWeight != null && userDob != null && userProfilePic != null) {
            return User(
                userId,
                userName,
                userEmail,
                userPassword,
                userRole,
                userHeight,
                userWeight,
                userDob,
                userProfilePic
            )
        } else {
            return null
        }
    }

    fun logIn(
        viewModel: LoginViewModel,
    ) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            viewModel.email.value.toString(),
            viewModel.password.value.toString()
        )
            .addOnSuccessListener { authResult ->
                viewModel._navigateToMain.value = true
            }
            .addOnFailureListener { exception ->
                FirebaseFirestore.getInstance().collection("users")
                    .whereEqualTo("email", viewModel.email.value.toString())
                    .whereEqualTo("password", viewModel.password.value.toString())
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // Matching user found in Firestore
                            // You can handle this case here
                            Log.d("MASUK AKWOKAW", "Ini pake firestore punya")
                            viewModel.emailError.value = null
                            viewModel.passwordError.value = null
                            viewModel._navigateToMain.value = true
                        } else {
                            // No matching user found
                            // Handle this case (e.g., show an error message)
                            viewModel.emailError.value = "Invalid Credentials"
                            viewModel.passwordError.value = "Invalid Credentials"
                            viewModel._navigateToMain.value = false
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Handle any errors
                    }
            }
    }

    fun findEmail(viewModel: ForgotPasswordViewModel) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("email", viewModel.email.value)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    viewModel._navigateToResetPassword.value = true;
                } else {
                    // Email does not exist in the "users" collection
                    // You can handle this case here
                    viewModel.emailError.value = "Email not found!"
                    viewModel._navigateToResetPassword.value = false;
                }
            }
            .addOnFailureListener { exception ->
                // Handle the failure, e.g., show an error message
                viewModel.emailError.value = "Email not found!"
                viewModel._navigateToResetPassword.value = false;
            }
    }

    fun resetPassword(viewModel: ResetPasswordViewModel) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("email", viewModel.currentEmail.value)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Iterate through the documents (there should be only one matching document)
                    for (document in querySnapshot.documents) {
                        // Get the document ID (UID of the user)
                        val uid = document.id

                        // Step 2: Update the password field
                        val userRef =
                            FirebaseFirestore.getInstance().collection("users").document(uid)
                        userRef.update("password", viewModel.password.value)
                            .addOnSuccessListener {
                                Log.d("Firestore Update", "Password updated successfully.")
                                viewModel.passwordError.value = null
                                viewModel.confirm_passwordError.value = null
                                viewModel._navigateToLoginPage.value = true
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore Update", "Error updating password: $e")
                            }
                    }
                } else {
//                        Log.d("Firestore Query", "No user found with email: $emailValue")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore Query", "Error querying Firestore: $e")
            }
    }

    fun changeName(userId: String, newName: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection.document(userId)
            .update("name", newName)
            .addOnSuccessListener {
                // The update was successful
                val curUser = FirebaseAuth.getInstance().currentUser
                val uid = curUser?.uid

                if (uid != null) {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = uid,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(context)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                            } else {
                                // User data not found
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                } else {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = userId,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(null)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                                Log.d("INI AUTH PAKE PUNYA MASUK", userModel.userPassword)
                            } else {
                                // User data not found
                                Log.d("Firestore", "User data not found")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    fun changeHeight(userId: String, newHeight: Int, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection.document(userId)
            .update("height", newHeight)
            .addOnSuccessListener {
                val curUser = FirebaseAuth.getInstance().currentUser
                val uid = curUser?.uid

                if (uid != null) {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = uid,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(context)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                                Log.d(
                                    "Height",
                                    userRepository.getCurrentUser()?.userHeight.toString()
                                )
                            } else {
                                // User data not found
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                } else {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = userId,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(null)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                                Log.d("INI AUTH PAKE PUNYA MASUK", userModel.userPassword)
                            } else {
                                // User data not found
                                Log.d("Firestore", "User data not found")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    fun changeWeight(userId: String, newWeight: Int, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection.document(userId)
            .update("weight", newWeight)
            .addOnSuccessListener {
                val curUser = FirebaseAuth.getInstance().currentUser
                val uid = curUser?.uid

                if (uid != null) {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = uid,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(context)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                            } else {
                                // User data not found
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                } else {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = userId,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(null)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                                Log.d("INI AUTH PAKE PUNYA MASUK", userModel.userPassword)
                            } else {
                                // User data not found
                                Log.d("Firestore", "User data not found")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    fun changePassword(userId: String, newPw: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        Log.d("PW", "Masuk kah manies")
        usersCollection.document(userId)
            .update("password", newPw)
            .addOnSuccessListener {
                val curUser = FirebaseAuth.getInstance().currentUser
                val uid = curUser?.uid

                if (uid != null) {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = uid,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(context)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                                Log.d(
                                    "PW Baru",
                                    userRepository.getCurrentUser()?.userPassword.toString()
                                )
                            } else {
                                // User data not found
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                } else {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(userId)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            if (documentSnapshot.exists()) {
                                // User data found in Firestore
                                val userData = documentSnapshot.data

                                val userModel = User(
                                    userId = userId,
                                    userName = userData?.get("name").toString(),
                                    userEmail = userData?.get("email").toString(),
                                    userPassword = userData?.get("password")
                                        .toString(), // You might not have the user's password here
                                    userRole = userData?.get("role").toString(),
                                    userHeight = (userData?.get("height") as? Long)?.toInt() ?: 0,
                                    userWeight = (userData?.get("weight") as? Long)?.toInt() ?: 0,
                                    userDob = userData?.get("dob").toString(),
                                    userProfilePic = userData?.get("userProfilePic").toString()
                                )

                                val userRepository = UserRepository(null)
                                userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                                Log.d("INI AUTH PAKE PUNYA MASUK", userModel.userPassword)
                            } else {
                                // User data not found
                                Log.d("Firestore", "User data not found")
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle Firestore query failure
                            Log.e("Firestore", "Error querying Firestore: $exception")
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
}