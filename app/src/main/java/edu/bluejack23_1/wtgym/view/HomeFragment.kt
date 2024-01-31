package edu.bluejack23_1.wtgym.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.FragmentHomeBinding
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.HomeViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userRepository: UserRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        userRepository = UserRepository(requireContext())

        Handler().postDelayed({
            val currentUser = userRepository.getCurrentUser()
            if (currentUser != null) {
                // User data found, you can access user properties
                val userName = currentUser.userName
                val userEmail = currentUser.userEmail
                val userRole = currentUser.userRole
                val userHeight = currentUser.userHeight
                val userWeight = currentUser.userWeight
                val userDob = currentUser.userDob

                // Update your UI with user data
                val greetingsTextView = binding.greetingsLbl
                greetingsTextView.text = "Hello, $userName"
            } else {
                // Handle the case when no user data is found
                // For example, display a message or redirect the user to log in.
            }
        }, 800) // 3000 milliseconds (3 seconds)
//        val greetingsTextView = binding.greetingsLbl
//        greetingsTextView.text = "Hello, ${sharedPreferences.getString("userName", null)}"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homePlanBtn.setOnClickListener {
            val data = "home"
            val exercisesFragment = ExercisesFragment.newInstance("home")

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, exercisesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.gymPlanBtn.setOnClickListener {
            val data = "gym"
            val exercisesFragment = ExercisesFragment.newInstance("gym")

            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, exercisesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }


//        viewModel.navigateToHomePlan.observe(viewLifecycleOwner, Observer {
//            if (it) {
//                // Create an instance of ExercisesFragment
//                val exercisesFragment = ExercisesFragment()
//
//                // Navigate to the ExercisesFragment using the fragment manager
//                val fragmentManager = childFragmentManager
//                val fragmentTransaction = fragmentManager.beginTransaction()
//                fragmentTransaction.replace(R.id.frameLayout, exercisesFragment)
//                fragmentTransaction.commit()
//            }
//        })


//        val greetingsTextView = binding.greetingsLbl
//        greetingsTextView.text = "Hello, ${sharedPreferences.getString("userName", null)}"
    }

    fun setGreetingsText(text: String) {
        binding.greetingsLbl.text = text
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}