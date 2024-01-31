package edu.bluejack23_1.wtgym

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.wtgym.databinding.ActivityMainBinding
import edu.bluejack23_1.wtgym.model.User
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.view.*

class MainActivity : AppCompatActivity() {
    //    val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var activityMainBinding: ActivityMainBinding

    var userNameString: String = ""

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        replaceFragment(HomeFragment())



        activityMainBinding.bottomNavbar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Handle the item with ID R.id.home
                    replaceFragment(HomeFragment())
                }
                R.id.favorite -> {
                    replaceFragment(FavoriteFragment())
                }
                R.id.location -> {
                    replaceFragment(GymLocationFragment())
                }
                R.id.user -> {
                    replaceFragment(MyAccountFragment())
                }
            }
            true
        }


        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val curUser = FirebaseAuth.getInstance().currentUser
        val uid = curUser?.uid
//
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
                            userProfilePic = userData?.get("profilePic").toString()
                        )

                        val userRepository = UserRepository(this)
                        userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences
                        Log.d("INI AUTH PAKE PUNYA MASUK", userModel.userPassword)


                        Log.d(
                            "ROLE",
                            userRepository.getCurrentUser()?.userRole + userRepository.getCurrentUser()?.userName
                        )

                        val bottomNavigationView =
                            findViewById<BottomNavigationView>(R.id.bottom_navbar)
                        val menu = bottomNavigationView.menu

                        if (userModel.userRole.toString() == "admin") {
                            menu.findItem(R.id.home).isVisible = true
                            menu.findItem(R.id.favorite).isVisible = false
                            menu.findItem(R.id.location).isVisible = false
                            menu.findItem(R.id.user).isVisible = true
                        } else {
                            menu.findItem(R.id.home).isVisible = true
                            menu.findItem(R.id.favorite).isVisible = true
                            menu.findItem(R.id.location).isVisible = true
                            menu.findItem(R.id.user).isVisible = true
                        }
                    } else {
                        // User data not found
                        Log.d("Firestore", "User data not found")
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle Firestore query failure
                    Log.e("Firestore", "Error querying Firestore: $exception")
                }
        } else {
            val email = intent.getStringExtra("email")
            FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]

                        val userModel = User(
                            userId = document.id,
                            userName = document.getString("name").orEmpty(),
                            userEmail = document.getString("email").orEmpty(),
                            userPassword = document.getString("password").orEmpty(),
                            userRole = document.getString("role").orEmpty(),
                            userHeight = (document.getLong("height") as? Long)?.toInt() ?: 0,
                            userWeight = (document.getLong("weight") as? Long)?.toInt() ?: 0,
                            userDob = (document.getString("dob").orEmpty()),
                            userProfilePic = (document.getString("profilePic").orEmpty())

                        )
                        Log.d("INI FIRE PAKE PUNYA MASUK", userModel.userPassword)
                        val userRepository = UserRepository(this)
                        userRepository.setCurrentUser(userModel) // Save the user data to SharedPreferences

                        Log.d(
                            "ROLE",
                            userRepository.getCurrentUser()?.userRole + userRepository.getCurrentUser()?.userName
                        )

                        val bottomNavigationView =
                            findViewById<BottomNavigationView>(R.id.bottom_navbar)
                        val menu = bottomNavigationView.menu

                        if (userModel.userRole.toString() == "admin") {
                            menu.findItem(R.id.home).isVisible = true
                            menu.findItem(R.id.favorite).isVisible = false
                            menu.findItem(R.id.location).isVisible = false
                            menu.findItem(R.id.user).isVisible = false
                        } else {
                            menu.findItem(R.id.home).isVisible = true
                            menu.findItem(R.id.favorite).isVisible = true
                            menu.findItem(R.id.location).isVisible = true
                            menu.findItem(R.id.user).isVisible = true
                        }
                    } else {
                        // Handle when no user data is found
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle the failure, e.g., show an error message
                    Log.e("Firestore", "Error querying Firestore: $exception")
                }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        // Implement your fragment replacement logic here
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}