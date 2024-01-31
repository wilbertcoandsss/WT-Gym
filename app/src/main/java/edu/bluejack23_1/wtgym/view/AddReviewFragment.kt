package edu.bluejack23_1.wtgym.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.databinding.FragmentAddReviewBinding
import edu.bluejack23_1.wtgym.model.GymLocation
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.AddReviewViewModel
import com.google.android.material.snackbar.Snackbar
import android.os.Handler
import android.os.Looper

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddReviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddReviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAddReviewBinding
    private lateinit var viewModel: AddReviewViewModel
    private lateinit var gymDetail: GymLocation
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddReviewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AddReviewViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.setFragmentReference(this)

        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_gym_location, container, false)
        return binding.root
    }

    private fun clearAllStarsColor() {
        binding.star1.clearColorFilter()
        binding.star2.clearColorFilter()
        binding.star3.clearColorFilter()
        binding.star4.clearColorFilter()
        binding.star5.clearColorFilter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRepository = UserRepository(requireContext())
        var profilePic: String = ""
        var name: String = ""
        var rating: Int = 0

        val currentUser = userRepository.getCurrentUser()
        if (currentUser != null) {
            // User data found, you can access user properties
            profilePic = currentUser.userProfilePic
            name = currentUser.userName
        }

        gymDetail = arguments?.getSerializable(AddReviewFragment.ARG_GYM_LOCATION) as GymLocation
        Log.d("AddReviewFragment", "Gym Detail: $gymDetail")

        binding.gymNameLbl.text = gymDetail.name
        binding.gymAddressLbl.text = gymDetail.address

        val imageUrl = profilePic
        Glide.with(requireContext())
            .load(imageUrl)
            .placeholder(R.drawable.circular_image)
            .into(binding.userImg)

        binding.star1.setOnClickListener {
            clearAllStarsColor()
            rating = 1
            viewModel.setStarData(1)
            binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
        }

        binding.star2.setOnClickListener {
            clearAllStarsColor()
            rating = 2
            viewModel.setStarData(2)
            binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
        }

        binding.star3.setOnClickListener {
            clearAllStarsColor()
            rating = 3
            viewModel.setStarData(3)
            binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
        }

        binding.star4.setOnClickListener {
            clearAllStarsColor()
            rating = 4
            viewModel.setStarData(4)
            binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
        }

        binding.star5.setOnClickListener {
            clearAllStarsColor()
            rating = 5
            viewModel.setStarData(5)
            binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            binding.star5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
        }

        binding.insertReviewBtn.setOnClickListener {
            binding.lblError.text = "";

            viewModel.onInsertButtonsClicked(gymDetail.uid, name, profilePic)
        }

        viewModel.reviewError.observe(viewLifecycleOwner, Observer { reviewError ->
            if (reviewError != null) {
                binding.lblError.text = reviewError;
            } else {

            }
        })

        viewModel.successfulReviewAddition.observe(viewLifecycleOwner) { success ->
            if (success) {
                showSuccessSnackbar()
                gymDetail.reviews.forEach { review ->
                    rating += review.rating
                    println("rating sebelumnya : " + review.rating)
                }

                val averageRating = (rating / (gymDetail.reviews.size+1)).toInt()

                Handler(Looper.getMainLooper()).postDelayed({
                    redirectToGymLocationDetail(averageRating)
                }, 3000)
            }
        }

    }

    private fun showSuccessSnackbar() {
        val snackbar = Snackbar.make(binding.root, "Review added successfully", Snackbar.LENGTH_LONG)
        snackbar.setAction("Dismiss") {
            // Handle the action click event here
        }
        snackbar.show()
    }


    private fun redirectToGymLocationDetail(rating: Int) {
        val gymDetail = GymLocationDetailFragment.newInstance(gymDetail, rating)

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, gymDetail)
        fragmentTransaction.addToBackStack(null) // Add this line if you want to add the transaction to the back stack
        fragmentTransaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddReviewFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val ARG_GYM_LOCATION = "gymLocation"

        @JvmStatic
        fun newInstance(gymLocation: GymLocation): AddReviewFragment {
            val fragment = AddReviewFragment()
            val args = Bundle()
            args.putSerializable(ARG_GYM_LOCATION, gymLocation)
            fragment.arguments = args
            return fragment
        }
    }
}