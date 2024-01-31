package edu.bluejack23_1.wtgym.model

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.atomic.AtomicInteger

@Keep
data class Exercise(
    var uid: String,
    val calories: Int,
    val description: String,
    val equipment: String, // New field for equipment
    @get:PropertyName("isGym") val isGym: Boolean, // Use @PropertyName to specify Firestore field nam
    val muscles: List<String>,
    val name: String,
    val steps: List<String>,
    val time: Int,
    val type: String, // New field for type
    val cover: String?,
) : Parcelable {
    constructor() : this("", 0, "", "", false, emptyList(), "", emptyList(), 0, "", "")

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeInt(calories)
        parcel.writeString(description)
        parcel.writeString(equipment) // Write equipment
        parcel.writeByte(if (isGym) 1 else 0) // Write isGym as a byte (boolean)
        parcel.writeStringList(muscles)
        parcel.writeString(name)
        parcel.writeStringList(steps)
        parcel.writeInt(time)
        parcel.writeString(type) // Write type
        parcel.writeSerializable(cover)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }
}

class ExerciseRepository() {
    private val db = FirebaseFirestore.getInstance()

    fun fetchAllExercises(callback: (List<Exercise>) -> Unit) {
        db.collection("exercises").get()
            .addOnSuccessListener { querySnapshot ->
                val exerciseList = mutableListOf<Exercise>()
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        val exercise = document.toObject(Exercise::class.java)
                        if (exercise != null) {
                            exerciseList.add(exercise)
                        }
                    }
                }
                callback(exerciseList)
            }
    }

    fun fetchExercisesWithLimit(
        limit: Int,
        isGymFilter: Boolean,
        callback: (List<Exercise>) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val exercisesRef = db.collection("exercises")

        // Define the query based on the isGym filter
        val query = if (isGymFilter) {
            exercisesRef.whereEqualTo("isGym", true)
        } else {
            exercisesRef.whereEqualTo("isGym", false)
        }

        query.limit(limit.toLong()).get()
            .addOnSuccessListener { result ->
                val exerciseList = ArrayList<Exercise>()

                for (document in result) {
                    val exercise = document.toObject(Exercise::class.java)
                    exercise.uid = document.id
                    exerciseList.add(exercise)
                }

                callback(exerciseList)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur
                callback(emptyList())
            }
    }

    fun getExercisesByMuscles(
        muscleList: List<String>,
        currentExerciseName: String,
        onSuccess: (QuerySnapshot) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
//        Log.d("Repo", muscleList.toString())
        db.collection("exercises")
            .whereArrayContainsAny("muscles", muscleList)
            .whereNotEqualTo("name", currentExerciseName)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d("Repo", "Query successful")
                for (document in querySnapshot.documents) {
                    val exercise = document.toObject(Exercise::class.java)
                    if (exercise != null) {
//                        Log.d("Repo", "Exercise UID: ${document.id}, Name: ${exercise.name}")
                    }
                }
                onSuccess(querySnapshot)
            }
            .addOnFailureListener { e ->
                Log.e("Repo", "Query failed", e)
                onFailure(e)
            }
    }

    fun addToFav(
        userId: String,
        exerciseId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val favoriteData = hashMapOf(
            "userId" to userId,
            "exercisesId" to exerciseId
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("favorites")
            .add(favoriteData)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun removeFavorite(
        userId: String,
        exerciseId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val favoriteRef = db.collection("favorites")
        favoriteRef.whereEqualTo("userId", userId)
            .whereEqualTo("exercisesId", exerciseId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    // Delete the favorite entry
                    favoriteRef.document(document.id).delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            onFailure(e)
                        }
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun isFavorite(
        userId: String,
        exerciseId: String,
        onSuccess: (Boolean) -> Unit, // Add this parameter
        onFailure: (Exception) -> Unit // Add this parameter
    ) {
        val favoritesRef = db.collection("favorites")
        val query =
            favoritesRef.whereEqualTo("userId", userId)
                .whereEqualTo("exercisesId", exerciseId)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                // If there are any documents matching the query, it's a favorite
                val isFavorite = !querySnapshot.isEmpty
                onSuccess(isFavorite) // Call the onSuccess callback
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur
                onFailure(exception) // Call the onFailure callback
            }
    }

    fun getUserFavorites(userId: String, limit: Int, callback: (List<Exercise>) -> Unit) {
        val favoritesRef = db.collection("favorites")
        val query = favoritesRef.whereEqualTo("userId", userId)

        query.limit(limit.toLong()).get()
            .addOnSuccessListener { querySnapshot ->
                val favoriteExercises = mutableListOf<Exercise>()

                val exerciseIds = mutableListOf<String>()

                // Extract exercise IDs from the favorite documents
                for (document in querySnapshot) {
                    val exerciseId = document.getString("exercisesId")
                    exerciseId?.let { exerciseIds.add(it) }
                }

                for (exerciseId in exerciseIds) {
                    db.collection("exercises")
                        .document(exerciseId)
                        .get()
                        .addOnSuccessListener { exerciseDocument ->
                            val exercise = exerciseDocument.toObject(Exercise::class.java)
                            exercise?.uid = exerciseDocument.id

                            exercise?.let {
                                favoriteExercises.add(it)
                            }

                            // When all exercises are fetched, invoke the callback
                            callback(favoriteExercises)

                        }
                        .addOnFailureListener { exception ->
                            // Handle any errors that occur while fetching exercise details
                            // You can choose to ignore or handle the error
                            callback(favoriteExercises)

                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur
                callback(emptyList()) // Consider not having favorites if there's an error
            }
    }

    fun deleteExercise(exerciseId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val exercisesRef = db.collection("exercises")

        exercisesRef.document(exerciseId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
