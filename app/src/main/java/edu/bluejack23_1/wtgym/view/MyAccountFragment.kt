package edu.bluejack23_1.wtgym.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.FragmentMyAccountBinding
import edu.bluejack23_1.wtgym.model.EventRepository
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.AccountViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyAccountFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentMyAccountBinding
    private lateinit var viewModel: AccountViewModel

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
        binding = FragmentMyAccountBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepository = UserRepository(this.requireContext())
        val yearString = userRepository.getCurrentUser()?.userDob?.takeLast(4)

        val year = yearString?.toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Calculate the age
        val age = currentYear - year!!

        binding.userIdentity.text = "${userRepository.getCurrentUser()?.userName}, ${age}"

        val height = userRepository.getCurrentUser()?.userHeight.toString() + " cm\nHeight"
        val weight = userRepository.getCurrentUser()?.userWeight.toString() + " kg\nWeight"

        val heightSpannable = SpannableString(height)
        val weightSpannable = SpannableString(weight)

        // Define the color for the "Height" and "Weight" parts
        val color = 0xFFA1A1A1.toInt() // Replace with your desired color
        val colorSpan = ForegroundColorSpan(color)

        // Apply the color span to the "Height" and "Weight" parts
        val heightStartIndex = height.indexOf("Height")
        val weightStartIndex = weight.indexOf("Weight")
        val heightEndIndex = heightStartIndex + "Height".length
        val weightEndIndex = weightStartIndex + "Weight".length

        heightSpannable.setSpan(
            colorSpan,
            heightStartIndex,
            heightEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        weightSpannable.setSpan(
            colorSpan,
            weightStartIndex,
            weightEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val customFont = resources.getFont(R.font.sf_light)
        binding.userHeight.typeface = customFont
        binding.userWeight.typeface = customFont

        binding.userHeight.text = heightSpannable
        binding.userWeight.text = weightSpannable

        val userHeightInCm =
            userRepository.getCurrentUser()?.userHeight?.toBigDecimal() ?: BigDecimal.ZERO
        val userWeightInKg =
            userRepository.getCurrentUser()?.userWeight?.toBigDecimal() ?: BigDecimal.ZERO

        val userHeightInM =
            userHeightInCm.divide(BigDecimal("100"), 2, RoundingMode.HALF_UP) // 2 is the scale

        val bmi =
            userWeightInKg.divide(userHeightInM.multiply(userHeightInM), 2, RoundingMode.HALF_UP)

        Log.d("Height", userHeightInCm.toString())


        val bmiCategory = when {
            bmi < BigDecimal("18.5") -> "Underweight"
            bmi < BigDecimal("25.0") -> "Normal"
            bmi < BigDecimal("30.0") -> "Overweight"
            bmi < BigDecimal("35.0") -> "Obese"
            else -> "Extremely Obese"
        }

        val text = "BMI Score: $bmi\nYour BMI : $bmiCategory"

        val spannableString = SpannableString(text)

        // Define the color for "BMI Score" and "Your BMI"
        val color1 = 0xFFA1A1A1.toInt() // Replace with your desired color
        val colorSpan1 = ForegroundColorSpan(color1)

        // Apply the color span to "BMI Score" and "Your BMI" parts
        val scoreStartIndex = text.indexOf("BMI Score")
        val yourStartIndex = text.indexOf("Your BMI")
        val scoreEndIndex = scoreStartIndex + "BMI Score".length
        val yourEndIndex = yourStartIndex + "Your BMI".length

        spannableString.setSpan(
            colorSpan,
            scoreStartIndex,
            scoreEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            colorSpan,
            yourStartIndex,
            yourEndIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the custom font
        binding.userBmi.typeface = customFont

        binding.userBmi.text = spannableString


        // Set an OnDateChangeListener for the CalendarView
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val bottomView = layoutInflater.inflate(R.layout.bottomevents_fragment, null)


            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)

            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())

            val date = inputFormat.parse(formattedDate)
            val formattedDate2 = outputFormat.format(date)

            bottomView.findViewById<TextView>(R.id.titleLbl).text = formattedDate2

            val userRepo = UserRepository(this.requireContext())
            val userId = userRepo.getCurrentUser()?.userId
            Log.d("Sekarang siapa", userId + userRepo.getCurrentUser()?.userName)

            val eventRepo = EventRepository()
            if (userId != null) {
                eventRepo.fetchEventsFromFirestore(formattedDate, userId) { events ->
                    val eventTextView = bottomView.findViewById<TextView>(R.id.eventLbl)

                    if (events.isNotEmpty()) {
                        val eventText = buildString {
                            for ((index, eventData) in events.withIndex()) {
                                val dateTime = eventData.eventDate
                                val time = dateTime.substring(dateTime.indexOf(' ') + 1)
                                append("${index + 1}. ${eventData.eventName} ($time)\n")
                            }
                        }
                        eventTextView.text = eventText
                    } else {
                        eventTextView.text = "No events for this date."
                    }
                }
            }

            var bottomSheetDialog: BottomSheetDialog

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()
        }

        binding.settingsBtn.setOnClickListener{
            val settingsFragment = SettingsFragment()

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, settingsFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.>
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyAccountFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyAccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}