package edu.bluejack23_1.wtgym.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.FragmentExercisesDetailBinding
import edu.bluejack23_1.wtgym.model.Exercise
import edu.bluejack23_1.wtgym.model.ExerciseRepository
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.ExercisesViewModel
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExercisesDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExercisesDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var isClicked = false
    private lateinit var binding: FragmentExercisesDetailBinding
    private lateinit var viewModel: ExercisesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentExercisesDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ExercisesViewModel::class.java)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val exercise = arguments?.getParcelable<Exercise>("exercise")

        val exerciseRepository = ExerciseRepository()
        if (exercise != null) {
            Log.d(
                "ExerciseDetailFragment",
                "Name: ${exercise.name}, Description: ${exercise.description}, Type: ${exercise.type}"
            )
            Glide.with(requireContext())
                .load(exercise.cover)
                .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
                .into(binding.exerciseCover)
            binding.exerciseNameLbl.text = exercise.name
            binding.timeLbl.text = "${exercise.time} mins"
            binding.burnLbl.text = "${exercise.calories} kcal"
            binding.descriptionLbl.text = "${exercise.description}"
            val stepsList = exercise.steps
            val stepsText = stepsList.joinToString("\n") // Join the list elements with line breaks
            binding.stepsLbl.text = stepsText

            val muscleList = exercise.muscles
            if (muscleList.size == 1) {
                binding.targetMuscles1.text = muscleList[0]
                val drawableId = resources.getIdentifier(
                    muscleList[0].lowercase(),
                    "drawable",
                    requireContext().packageName
                )
                binding.targetMuscles1img.setImageResource(drawableId)

                binding.targetMuscles2img.visibility = View.GONE
                binding.targetMuscles2.visibility = View.GONE
                binding.targetMuscles3.visibility = View.GONE
                binding.targetMuscles3img.visibility = View.GONE
            } else if (muscleList.size == 2) {
                binding.targetMuscles1.text = muscleList[0]
                binding.targetMuscles2.text = muscleList[1]

                val drawableId1 = resources.getIdentifier(
                    muscleList[0].lowercase(),
                    "drawable",
                    requireContext().packageName
                )
                binding.targetMuscles1img.setImageResource(drawableId1)

                val drawableId2 = resources.getIdentifier(
                    muscleList[1].lowercase(),
                    "drawable",
                    requireContext().packageName
                )
                binding.targetMuscles2img.setImageResource(drawableId2)


                binding.targetMuscles3.visibility = View.GONE
                binding.targetMuscles3img.visibility = View.GONE

            } else if (muscleList.size == 3) {
                binding.targetMuscles1.text = muscleList[0]
                binding.targetMuscles2.text = muscleList[1]
                binding.targetMuscles3.text = muscleList[2]

                val drawableId1 = resources.getIdentifier(
                    muscleList[0].lowercase(),
                    "drawable",
                    requireContext().packageName
                )
                binding.targetMuscles1img.setImageResource(drawableId1)

                val drawableId2 = resources.getIdentifier(
                    muscleList[1].lowercase(),
                    "drawable",
                    requireContext().packageName
                )
                binding.targetMuscles2img.setImageResource(drawableId2)

                val drawableId3 = resources.getIdentifier(
                    muscleList[2].lowercase(),
                    "drawable",
                    requireContext().packageName
                )
                binding.targetMuscles3img.setImageResource(drawableId3)
            }

            exerciseRepository.getExercisesByMuscles(muscleList, exercise.name,
                onSuccess = { querySnapshot ->
                    val documentList = querySnapshot.documents

                    // Determine the total size
                    val size = documentList.size
                    Log.d("Exercise", "Total Size: $size")

                    // Limit the data to the top 3
                    val limit = 3
                    val top3Documents =
                        if (size > limit) documentList.subList(0, limit) else documentList

                    if (top3Documents.size == 1) {
                        val exercise1 = top3Documents[0].toObject(Exercise::class.java)
                        if (exercise1 != null) {
                            val uid1 = exercise1.uid
                            val name1 = exercise1.name
                            Log.d("Exercise", "UID 1: ${top3Documents[0].id}, Name 1: $name1")

                            binding.alternative1.text = name1

                            Glide.with(requireContext())
                                .load(exercise1.cover)
                                .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
                                .into(binding.alternative1img)
                        }


                        binding.alternative2.visibility = View.GONE
                        binding.alternative2img.visibility = View.GONE

                        binding.alternative3.visibility = View.GONE
                        binding.alternative3img.visibility = View.GONE

                    } else if (top3Documents.size == 2) {
                        // Access the first exercise (index 0)
                        val exercise1 = top3Documents[0].toObject(Exercise::class.java)
                        if (exercise1 != null) {
                            val uid1 = exercise1.uid
                            val name1 = exercise1.name
                            Log.d("Exercise", "UID 1: ${top3Documents[0].id}, Name 1: $name1")

                            binding.alternative1.text = name1

                            Glide.with(requireContext())
                                .load(exercise1.cover)
                                .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
                                .into(binding.alternative1img)
                        }

                        // Access the second exercise (index 1)
                        val exercise2 = top3Documents[1].toObject(Exercise::class.java)
                        if (exercise2 != null) {
                            val uid2 = exercise2.uid
                            val name2 = exercise2.name
                            Log.d("Exercise", "UID 2: ${top3Documents[1].id}, Name 2: $name2")

                            binding.alternative2.text = name2

                            Glide.with(requireContext())
                                .load(exercise2.cover)
                                .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
                                .into(binding.alternative2img)
                        }

                        binding.alternative3.visibility = View.GONE
                        binding.alternative3img.visibility = View.GONE
                    } else if (top3Documents.size == 3) {
                        // Access the first exercise (index 0)
                        val exercise1 = top3Documents[0].toObject(Exercise::class.java)
                        if (exercise1 != null) {
                            val uid1 = exercise1.uid
                            val name1 = exercise1.name
                            Log.d("Exercise", "UID 1: ${top3Documents[0].id}, Name 1: $name1")

                            binding.alternative1.text = name1

                            Glide.with(requireContext())
                                .load(exercise1.cover)
                                .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
                                .into(binding.alternative1img)
                        }

                        // Access the second exercise (index 1)
                        val exercise2 = top3Documents[1].toObject(Exercise::class.java)
                        if (exercise2 != null) {
                            val uid2 = exercise2.uid
                            val name2 = exercise2.name
                            Log.d("Exercise", "UID 2: ${top3Documents[1].id}, Name 2: $name2")

                            binding.alternative2.text = name2

                            Glide.with(requireContext())
                                .load(exercise2.cover)
                                .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
                                .into(binding.alternative2img)
                        }

                        val exercise3 = top3Documents[2].toObject(Exercise::class.java)
                        if (exercise3 != null) {
                            val uid2 = exercise3.uid
                            val name3 = exercise3.name
                            Log.d("Exercise", "UID 3: ${top3Documents[2].id}, Name 3: $name3")


                            Glide.with(requireContext())
                                .load(exercise3.cover)
                                .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
                                .into(binding.alternative3img)

                            binding.alternative3.text = name3
                        }
                    }
                },
                onFailure = { e ->
                    // Handle errors
                }
            )


            val userRepo = UserRepository(this.requireContext())
            val exerRepo = ExerciseRepository()

            val userId = userRepo.getCurrentUser()?.userId.toString()
            val exerciseId = exercise.uid

            exerciseRepository.isFavorite(userId, exerciseId,
                onSuccess = { isFavorite ->
                    Log.d("BOOL", isFavorite.toString())
                    // Set the initial state based on isFavorite
                    isClicked = isFavorite
                    // Update the icon tint based on isFavorite
                    updateIconTint()
                },
                onFailure = { e ->
                    // Handle failure
                }
            )

            binding.favIcon.setOnClickListener {
                if (isClicked) {
                    // Remove from Fav
                    binding.favIcon.setColorFilter(Color.WHITE)

                    exerciseRepository.removeFavorite(userId, exerciseId,
                        onSuccess = {
                            // Handle successful removal
                        },
                        onFailure = { e ->
                            // Handle removal failure
                        }
                    )

                    val snackbar =
                        Snackbar.make(
                            requireView(),
                            "Removed from your favorites!",
                            Snackbar.LENGTH_SHORT
                        )
                    snackbar.setBackgroundTint(Color.parseColor("#3A3B3C"))
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                    snackbar.setAction("Dismiss") {
                        // Handle the action click event here
                    }

                    snackbar.show()
                } else {
                    // Add To Fav
                    binding.favIcon.setColorFilter(Color.RED)

                    exerciseRepository.addToFav(userId, exerciseId,
                        onSuccess = {
                            // Handle successful addition
                        },
                        onFailure = { e ->
                            // Handle addition failure
                        }
                    )
                    val snackbar =
                        Snackbar.make(
                            requireView(),
                            "Added to your favorites!",
                            Snackbar.LENGTH_SHORT
                        )
                    snackbar.setBackgroundTint(Color.parseColor("#3A3B3C"))
                    snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                    snackbar.setAction("Dismiss") {
                        // Handle the action click event here
                    }

                    snackbar.show()
                }
                // Toggle the click state
                isClicked = !isClicked
            }


            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentTime = Calendar.getInstance()
            val selectedDate = dateFormat.format(currentTime.time)
            val selectedExerciseId = exercise.name  // Replace this with your selected exercise ID
            val selectedUserId = userId  // Replace this with your selected user ID

            val db = FirebaseFirestore.getInstance()
            val eventsRef = db.collection("events")

            eventsRef
                .whereEqualTo("userId", selectedUserId)
                .whereEqualTo("eventName", selectedExerciseId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    // If there's a document, the workout has already been completed today
                    if (!querySnapshot.isEmpty) {
                        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
                        val currentMinute = currentTime.get(Calendar.MINUTE)
                        val currentSecond = currentTime.get(Calendar.SECOND)

                        // If the current time is after midnight (00:00:00), enable the button
                        if (currentHour > 0 || currentMinute > 0 || currentSecond > 0) {
                            binding.doneBtn.isEnabled = false
                            binding.doneBtn.setBackgroundColor(Color.GREEN)
                            binding.doneBtn.setTextColor(Color.WHITE)
                        } else {
                            binding.doneBtn.isEnabled = true
                            binding.doneBtn.setBackgroundColor(R.color.primary_btn)
                        }
                    } else {
                        // Enable the button
                        binding.doneBtn.isEnabled = true
                        binding.doneBtn.setBackgroundColor(R.color.primary_btn) // Set the desired color
                    }
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }

            binding.doneBtn.setOnClickListener {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val currentTime = Calendar.getInstance()

                val selectedDate = dateFormat.format(currentTime.time)
                val selectedTime = timeFormat.format(currentTime.time)

                val selectedExerciseId =
                    exercise.name  // Replace this with your selected exercise ID
                val selectedUserId = userId  // Replace this with your selected user ID

                val dateTime = "$selectedDate $selectedTime"
                saveEventToFirestore(selectedDate, selectedTime, selectedExerciseId, selectedUserId)

                // Define a Firestore query to check if the user has already completed the workout for the current day
                val db = FirebaseFirestore.getInstance()
                val eventsRef = db.collection("events")

                eventsRef
                    .whereEqualTo("userId", selectedUserId)
                    .whereEqualTo("eventName", selectedExerciseId)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        // If there's a document, the workout has already been completed today
                        if (!querySnapshot.isEmpty) {
                            // Disable the button
                            binding.doneBtn.isEnabled = false
                            binding.doneBtn.setBackgroundColor(Color.GREEN)
                        } else {
                            // If the date is not today, enable the button
                            if (selectedDate != dateFormat.format(Calendar.getInstance().time)) {
                                // Enable the button
                                binding.doneBtn.isEnabled = true
                                binding.doneBtn.setBackgroundColor(R.color.primary_btn) // Set the desired color
                            } else {
                                // Enable the button
                                binding.doneBtn.isEnabled = true
                                binding.doneBtn.setBackgroundColor(R.color.primary_btn) // Set the desired color
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                    }

                val snackbar =
                    Snackbar.make(
                        requireView(),
                        "Added to your workout history!",
                        Snackbar.LENGTH_SHORT
                    )
                snackbar.setBackgroundTint(Color.parseColor("#3A3B3C"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                snackbar.setAction("Dismiss") {
                    // Handle the action click event here
                }

                snackbar.show()
            }
        }
    }

    private fun saveEventToFirestore(
        selectedDate: String,
        selectedTime: String,
        selectedExerciseId: String,
        selectedUserId: String
    ) {
        val dateTime = "$selectedDate $selectedTime"

        val eventData = hashMapOf(
            "eventName" to selectedExerciseId,
            "eventDate" to dateTime,
            "userId" to selectedUserId,
        )

        val db = FirebaseFirestore.getInstance()
        db.collection("events")
            .add(eventData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")
                // Optionally, show a success message to the user

            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
                // Optionally, show an error message to the user
            }
    }

    private fun updateIconTint() {
        val iconColor = if (isClicked) {
            Color.RED // Set the color to red if clicked (favorite)
        } else {
            Color.WHITE // Set the color to white if not clicked (not a favorite)
        }

        binding.favIcon.setColorFilter(iconColor)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExercisesDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(exercise: Exercise): ExercisesDetailFragment {
            val fragment = ExercisesDetailFragment()
            val args = Bundle()
            args.putParcelable("exercise", exercise)
            fragment.arguments = args
            return fragment
        }
    }
}