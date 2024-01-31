package edu.bluejack23_1.wtgym.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.wtgym.view.AddReviewFragment

class AddReviewViewModel : ViewModel() {

    private val _starData = MutableLiveData<Int>(0)
    val starData: LiveData<Int>
        get() = _starData

    private val _successfulReviewAddition = MutableLiveData<Boolean>()
    val successfulReviewAddition: LiveData<Boolean>
        get() = _successfulReviewAddition

    val review = MutableLiveData<String>()
    val reviewError = MutableLiveData<String?>()

    fun setStarData(stars: Int) {
        _starData.value = stars
    }

    private lateinit var fragment: AddReviewFragment

    fun setFragmentReference(fragment: AddReviewFragment) {
        this.fragment = fragment
    }

    fun onInsertButtonsClicked(gymId: String, name: String, profilePic: String){
        val ratingValue = _starData.value
        val reviewValue = review.value

        reviewError.value = null

        if (reviewValue.isNullOrBlank()) {
            reviewError.value = "Review is required!"
            return
        }

        reviewError.value = null


        val data = hashMapOf(
            "gymId" to gymId,
            "name" to name,
            "profilePic" to profilePic,
            "rating" to ratingValue,
            "review" to reviewValue
        )

        FirebaseFirestore.getInstance().collection("reviews")
            .add(data)
            .addOnSuccessListener {
                _successfulReviewAddition.value = true

                Log.d("Firestore", "DocumentSnapshot successfully written!")
                // You can perform further actions here after successfully storing the user's information
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing document", e)
                // Handle any errors that may occur during the document writing process
            }

    }


}