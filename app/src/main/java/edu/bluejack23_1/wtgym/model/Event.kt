package edu.bluejack23_1.wtgym.model

import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class EventData(
    val eventDate: String,
    val eventName: String
)

class EventRepository(){
    private val db = FirebaseFirestore.getInstance()

    fun fetchEventsFromFirestore(selectedDate: String, selectedUserId: String, onComplete: (List<EventData>) -> Unit) {
        // Define the start and end date strings for the selected date
        val startDate = "$selectedDate 00:00:00"
        val endDate = "$selectedDate 23:59:59"

        val events = mutableListOf<EventData>()

        FirebaseFirestore.getInstance().collection("events")
            .whereGreaterThanOrEqualTo("eventDate", startDate)
            .whereLessThanOrEqualTo("eventDate", endDate)
            .whereEqualTo("userId", selectedUserId) // Filter by userId
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    // Extract event data and create EventData object
                    val eventDate = document.getString("eventDate")
                    val eventName = document.getString("eventName")

                    if (eventDate != null && eventName != null) {
                        val eventData = EventData(eventDate, eventName)
                        events.add(eventData)
                    }
                }

                onComplete(events)
            }
            .addOnFailureListener { e ->
                onComplete(emptyList())
            }
    }
}