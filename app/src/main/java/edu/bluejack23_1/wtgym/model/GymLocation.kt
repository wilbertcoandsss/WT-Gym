package edu.bluejack23_1.wtgym.model

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

data class Reviews(
    val rating: Int = 0,
    val name: String = "",
    val review: String = "",
    val profilePic: String = "",
    val gymId: String = ""
) : Serializable {
    constructor() : this(0, "", "", "", "")
}

data class GymLocation(
    var uid: String,
    val address: String = "",
    val pictures: List<String> = emptyList(),
    val name: String = "",
    val city: String = "",
    val reviews: List<Reviews> = emptyList(),
    var totalRating: Int = 0
) : Serializable {
    // your existing methods here
    constructor() : this("", "", emptyList(), "", "", emptyList(), 0)
}

class GymLocationRepository {

    private val db = FirebaseFirestore.getInstance()

    fun fetchAllGymLocations(callback: (List<GymLocation>) -> Unit) {
        db.collection("gym").get().addOnSuccessListener { gymSnapshot ->
            val gymList = mutableListOf<GymLocation>()
            if (!gymSnapshot.isEmpty) {
                for (gymDocument in gymSnapshot.documents) {
                    val gym = gymDocument.toObject(GymLocation::class.java)
                    gym?.let { gl ->
                        db.collection("reviews")
                            .whereEqualTo("gymId", gymDocument.id)
                            .get()
                            .addOnSuccessListener { reviewSnapshot ->
                                val reviewsList = mutableListOf<Reviews>()
                                var totalRating = 0
                                if (!reviewSnapshot.isEmpty) {
                                    for (reviewDocument in reviewSnapshot.documents) {
                                        val review = reviewDocument.toObject(Reviews::class.java)
                                        review?.let { reviewsList.add(review) }

                                        totalRating += review!!.rating

                                    }
                                }
                                val averageRating = if (reviewsList.isNotEmpty()) (totalRating / reviewsList.size).toInt() else 0
                                gymList.add(gl.copy(reviews = reviewsList, totalRating = averageRating))
                                callback(gymList)
                            }
                    }
                }
            }
        }
    }

    fun fetchGymLocationWithLimit(
        limit: Int,
        callback: (List<GymLocation>) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()

        db.collection("gym").limit(limit.toLong()).get()
            .addOnSuccessListener { result ->
                val gymLocationList = ArrayList<GymLocation>()

                val totalCount = result.documents.size
                var currentCount = 0

                for (document in result) {
                    val gymLocation = document.toObject(GymLocation::class.java)
                    gymLocation.uid = document.id
                    fetchReviewsForGymLocation(db, gymLocation) { reviewsList ->
                        val totalRating = reviewsList.map { it.rating }.average().toInt()
                        val updatedGymLocation = gymLocation.copy(reviews = reviewsList, totalRating = totalRating)
                        gymLocationList.add(updatedGymLocation)

                        currentCount++
                        if (currentCount == totalCount) {
                            callback(gymLocationList.sortedBy { it.uid })
                        }
                    }
                }

            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur
                callback(emptyList())
            }
    }

    private fun fetchReviewsForGymLocation(db: FirebaseFirestore, gymLocation: GymLocation, reviewsCallback: (List<Reviews>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("gymId", gymLocation.uid)
            .get()
            .addOnSuccessListener { reviewSnapShot ->
                val reviewsList = mutableListOf<Reviews>()
                for (reviewDocument in reviewSnapShot.documents) {
                    val review = reviewDocument.toObject(Reviews::class.java)
                    review?.let { reviewsList.add(review) }
                }
                reviewsCallback(reviewsList)
            }
    }

    fun fetchAllReviewByGymId(gymId: String, callback: (List<Reviews>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("gymId", gymId)
            .get()
            .addOnSuccessListener { reviewSnapshot ->
                val reviewsList = mutableListOf<Reviews>()
                for (reviewDocument in reviewSnapshot.documents) {
                    val review = reviewDocument.toObject(Reviews::class.java)
                    review?.let { reviewsList.add(it) }
                }
                callback(reviewsList)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur
                callback(emptyList())
            }
    }



}