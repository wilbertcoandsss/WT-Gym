package edu.bluejack23_1.wtgym.view

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.FragmentEditExercisesBinding
import edu.bluejack23_1.wtgym.model.Exercise
import edu.bluejack23_1.wtgym.model.ExerciseRepository
import edu.bluejack23_1.wtgym.viewmodel.ExercisesViewModel
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditExercisesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditExercisesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentEditExercisesBinding
    private lateinit var viewModel: ExercisesViewModel

    private val REQUEST_IMAGE_PICK = 1
    val config: MutableMap<String, String> = mutableMapOf()
    var selectedImageUri: Uri? = null

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
        binding = FragmentEditExercisesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ExercisesViewModel::class.java)
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_gym_location, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val exercise = arguments?.getParcelable<Exercise>("exercise")

        val exerciseRepository = ExerciseRepository()

        binding.uploadBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*" // You can restrict to image types

            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        binding.dropdownMuscles.setOnClickListener {
            Log.d("Afah iyah gamasuk", "Kok aneh gamasuk")
            val dialog = AlertDialog.Builder(requireContext())

            val muscles = arrayOf("Abdominal", "Back", "Arms", "Legs", "Chest")

            val selectedList = ArrayList<Int>()
            val checkedItems = BooleanArray(muscles.size) // Keeps track of selected items
            val selectedStrings = ArrayList<String>()

            dialog.setTitle("Target Muscles")
            dialog.setMultiChoiceItems(muscles, checkedItems) { _, which, isChecked ->
                if (isChecked) {
                    selectedList.add(which)
                } else if (selectedList.contains(which)) {
                    selectedList.remove(which)
                }
                checkedItems[which] = isChecked
            }

            dialog.setPositiveButton("Done") { _, _ ->

                for (j in selectedList.indices) {
                    selectedStrings.add(muscles[selectedList[j]])
                }

                val formattedText =
                    selectedStrings.joinToString(", ") // Convert the list to a formatted string

                if (formattedText.isNotEmpty()) {
                    binding.dropdownMuscles.text = formattedText
                } else {
                    binding.dropdownMuscles.text = "Select Target Muscles"
                }
            }

            dialog.setNeutralButton("Clear All") { _, _ ->
                // Clear all selections and update the checkedItems array
                selectedList.clear()
                checkedItems.fill(false)
                selectedStrings.clear()

                binding.dropdownMuscles.text = "Select Target Muscles"
            }

            dialog.show()
        }

        binding.dropdownType.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())

            val type = arrayOf("Home", "Gym")
            var selectedType = -1 // Initialize with no selection

            dialog.setTitle("Workout Type")
            dialog.setSingleChoiceItems(type, selectedType) { _, which ->
                selectedType = which
            }

            dialog.setPositiveButton("Done") { _, _ ->
                if (selectedType != -1) {
                    // A type has been selected
                    val selectedTypeText = type[selectedType]

                    binding.dropdownType.text = selectedTypeText

                    if (selectedTypeText == "Gym") {
                        binding.equipmentLbl.visibility = View.VISIBLE
                        binding.dropdownEquipment.visibility = View.VISIBLE
                    } else {
                        if (selectedTypeText == "Home")
                            binding.equipmentLbl.visibility = View.GONE
                        binding.dropdownEquipment.visibility = View.GONE
                    }
                } else {
                    // No type selected
                    binding.dropdownType.text = "Select Workout Type"
                }
            }

            dialog.setNeutralButton("Clear Selection") { _, _ ->
                selectedType = -1 // Clear the selection
                binding.dropdownType.text = "Select Workout Type"
            }

            dialog.show()
        }

        binding.dropdownEquipment.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())

            val type = arrayOf("Leverage Machine", "Dumbell", "Body Weight")
            var selectedType = -1 // Initialize with no selection

            dialog.setTitle("Workout Equipment")
            dialog.setSingleChoiceItems(type, selectedType) { _, which ->
                selectedType = which
            }

            dialog.setPositiveButton("Done") { _, _ ->
                if (selectedType != -1) {
                    // A type has been selected
                    val selectedTypeText = type[selectedType]
                    binding.dropdownEquipment.text = selectedTypeText
                } else {
                    // No type selected
                    binding.dropdownEquipment.text = "Select Workout Equipment"
                }
            }

            dialog.setNeutralButton("Clear Selection") { _, _ ->
                selectedType = -1 // Clear the selection
                binding.dropdownEquipment.text = "Select Workout Equipment"
            }

            dialog.show()
        }

        binding.dropdownVariation.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())

            val type = arrayOf("Strength", "Cardio", "Stretching")
            var selectedType = -1 // Initialize with no selection

            dialog.setTitle("Workout Variation")
            dialog.setSingleChoiceItems(type, selectedType) { _, which ->
                selectedType = which
            }

            dialog.setPositiveButton("Done") { _, _ ->
                if (selectedType != -1) {
                    // A type has been selected
                    val selectedTypeText = type[selectedType]
                    binding.dropdownVariation.text = selectedTypeText
                } else {
                    // No type selected
                    binding.dropdownVariation.text = "Select Workout Variation"
                }
            }

            dialog.setNeutralButton("Clear Selection") { _, _ ->
                selectedType = -1 // Clear the selection
                binding.dropdownVariation.text = "Select Workout Variation"
            }

            dialog.show()
        }

        binding.updateExercisesBtn.setOnClickListener {
            val exerciseTimeText = binding.exerTimeLbl.text.toString().trim()
            val exerciseTime = exerciseTimeText.toIntOrNull()

            val exerciseCaloriesText = binding.exerCaloriesLbl.text.toString().trim()
            val exerciseCalories = exerciseCaloriesText.toIntOrNull()

            val nameError = "Exercise name must be not empty!"
            val timeError = "Exercise time cannot be empty or minus!"
            val calError = "Exercise calories cannot be empty or minus!"
            val descError = "Exercise description cannot be empty!"
            val stepError = "Exercise steps cannot be empty!"
            val muscleError = "Select at least one muscles!"
            val imgError = "Select at least one picture!"
            val typeError = "Select at least one type!"
            val equipmentError = "Select one equipment!"
            val variationError = "Select one variation!"

            var isError = false;


            Log.d("Muscles", binding.dropdownMuscles.text.toString())
            if (binding.exerNameLbl.text.isNullOrBlank()) {
                binding.exerNameLbl.error = nameError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.exerNameLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        nameError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }

            if (exerciseTime == null || exerciseTime!! <= 0) {
                binding.exerTimeLbl.error = timeError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.exerTimeLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        timeError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()

                return@setOnClickListener
            }

            if (exerciseCalories == null || exerciseCalories <= 0) {
                binding.exerCaloriesLbl.error = calError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.exerCaloriesLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        calError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()

                return@setOnClickListener
            }

            if (binding.exerDescLbl.text.isNullOrBlank()) {
                binding.exerDescLbl.error = descError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.exerDescLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        descError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }

            if (binding.exerStepsLbl.text.isNullOrBlank()) {
                binding.exerStepsLbl.error = stepError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.exerStepsLbl.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        stepError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }

            if (binding.dropdownType.text == "Select Workout Type") {
                binding.dropdownType.error = typeError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.dropdownType.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        typeError,
                        Snackbar.LENGTH_SHORT
                    )

                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }
            if (binding.dropdownMuscles.text == "Select Target Muscles") {
                // If "Select Muscles" is still displayed, it means no selection was made.
                binding.dropdownMuscles.error = muscleError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.dropdownMuscles.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        muscleError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }
            if (binding.dropdownEquipment.text == "Select Workout Equipment") {
                binding.dropdownEquipment.error = equipmentError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.dropdownEquipment.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        equipmentError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }
            if (binding.dropdownVariation.text == "Select Workout Variation") {
                binding.dropdownVariation.error = variationError

                val paddingInPixels = resources.getDimensionPixelSize(R.dimen.padding_size)
                binding.dropdownVariation.setPadding(paddingInPixels, 0, paddingInPixels, 0)

                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        variationError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }

            if (binding.previewImg.drawable == null) {
                binding.uploadBtn.error = imgError
                isError = true;
                val snackbar =
                    Snackbar.make(
                        requireView(),
                        imgError,
                        Snackbar.LENGTH_SHORT
                    )


                snackbar.setBackgroundTint(Color.parseColor("#FFFFFF"))
                snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
                snackbar.setTextColor(Color.parseColor("#FF0000"))

                snackbar.show()
                return@setOnClickListener
            }
            if (isError == false) {
                val pattern = "\\d+\\.\\s+"
                val items = binding.exerStepsLbl.text.toString().split(Regex(pattern))

                val filteredItems = items.filter { it.isNotBlank() }.map { it.trim() }

                binding.progressBar.visibility = View.VISIBLE
                binding.loadingLbl.visibility = View.VISIBLE

                var imageUrl: String? = null
                // Uplaod Image
                selectedImageUri?.let { uri ->
                    MediaManager.get().upload(uri)
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String?) {

                            }

                            override fun onProgress(
                                requestId: String?,
                                bytes: Long,
                                totalBytes: Long
                            ) {

                            }

                            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                                // Handle the upload success
                                imageUrl = resultData["url"] as String

                                // You can display or use the Cloudinary URL here.
                                // Assuming you have a reference to your Firestore database
                                val db = FirebaseFirestore.getInstance()


                                val exerciseId = exercise?.uid

                                if (exerciseId != null) {
                                    // Define the exercise data to be updated
                                    val updatedExerciseData = hashMapOf(
                                        "calories" to exerciseCalories,  // Replace exerciseCalories with the actual value
                                        "description" to binding.exerDescLbl.text.toString(),
                                        "equipment" to binding.dropdownEquipment.text.toString(),
                                        "isGym" to (binding.dropdownType.text.toString() == "Gym"),  // Check if it's "Gym"
                                        "muscles" to binding.dropdownMuscles.text.toString()
                                            .split(", "),  // Convert to an array of strings
                                        "name" to binding.exerNameLbl.text.toString(),
                                        "steps" to filteredItems,  // Array of strings obtained earlier
                                        "time" to exerciseTime!!,  // Assuming exerciseTime is not null
                                        "type" to binding.dropdownType.text.toString(),
                                        "cover" to imageUrl
                                    )

                                    // Reference the specific exercise document by its ID and update its data
                                    val exerciseRef =
                                        db.collection("exercises").document(exerciseId)
                                    exerciseRef.update(updatedExerciseData)

                                    val snackbar = Snackbar.make(
                                        requireView(),
                                        "Successfully Updated Exercises",
                                        Snackbar.LENGTH_SHORT
                                    )

                                    snackbar.setBackgroundTint(Color.parseColor("#3A3B3C"))
                                    snackbar.animationMode =
                                        BaseTransientBottomBar.ANIMATION_MODE_FADE
                                    snackbar.setTextColor(Color.parseColor("#FFFFFF"))

                                    snackbar.setAction("Dismiss") {
                                        // Handle the action click event here
                                    }

                                    snackbar.show()

                                    binding.progressBar.visibility = View.GONE
                                    binding.loadingLbl.visibility = View.GONE

                                    Handler().postDelayed({
                                        if (param1.toString() == "gym") {
                                            val exercisesFragment = ExercisesFragment.newInstance("gym")
                                            val fragmentManager =
                                                requireActivity().supportFragmentManager
                                            val fragmentTransaction = fragmentManager.beginTransaction()
                                            fragmentTransaction.replace(
                                                R.id.frameLayout,
                                                exercisesFragment
                                            )
                                            fragmentTransaction.commit()
                                        } else {
                                            val exercisesFragment =
                                                ExercisesFragment.newInstance("home")
                                            val fragmentManager =
                                                requireActivity().supportFragmentManager
                                            val fragmentTransaction = fragmentManager.beginTransaction()
                                            fragmentTransaction.replace(
                                                R.id.frameLayout,
                                                exercisesFragment
                                            )
                                            fragmentTransaction.commit()
                                        }
                                    }, 1050) // 3000 milliseconds (3 seconds)
                                }
                            }

                            override fun onError(requestId: String, error: ErrorInfo) {
                                // Handle the upload error
                            }

                            override fun onReschedule(requestId: String?, error: ErrorInfo?) {

                            }
                        }).dispatch()
                }


                // Ini Insert
//
//                val db = FirebaseFirestore.getInstance()
//
//                val exerciseData = hashMapOf(
//                    "calories" to exerciseCalories,  // Replace exerciseCalories with the actual value
//                    "description" to binding.exerDescLbl.text.toString(),
//                    "equipment" to binding.dropdownEquipment.text.toString(),
//                    "isGym" to (binding.dropdownType.text.toString() == "Gym"),  // Check if it's "Gym"
//                    "muscles" to binding.dropdownMuscles.text.toString()
//                        .split(", "),  // Convert to array of strings
//                    "name" to binding.exerNameLbl.text.toString(),
//                    "steps" to filteredItems,  // Array of strings obtained earlier
//                    "time" to exerciseTime!!,  // Assuming exerciseTime is not null
//                    "type" to binding.dropdownType.text.toString()
//                )
//
//// Add a new exercise document to Firestore
//                val exercisesCollection = db.collection("exercises")
//                val newExerciseRef: Task<DocumentReference> = exercisesCollection.add(exerciseData)

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            if (data != null) {
                // Handle the selected image here
                // You can display the selected image, upload it to Cloudinary, or perform other actions.
                Log.d("Selected Image", selectedImageUri.toString())

                selectedImageUri = data.data // Store the selected image URI
                binding.previewImg.setImageURI(selectedImageUri)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditExercisesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(exercise: Exercise): EditExercisesFragment {
            val fragment = EditExercisesFragment()
            val args = Bundle()
            args.putParcelable("exercise", exercise)
            fragment.arguments = args
            return fragment
        }
    }
}