package edu.bluejack23_1.wtgym.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.adapter.ReviewAdapter
import edu.bluejack23_1.wtgym.databinding.FragmentGymLocationDetailBinding
import edu.bluejack23_1.wtgym.model.GymLocation
import edu.bluejack23_1.wtgym.model.GymLocationRepository
import edu.bluejack23_1.wtgym.model.Reviews
import edu.bluejack23_1.wtgym.viewmodel.GymLocationDetailViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GymLocationDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GymLocationDetailFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentGymLocationDetailBinding
    private lateinit var viewModel: GymLocationDetailViewModel

    private lateinit var gymDetail: GymLocation

    private lateinit var recyclerView: RecyclerView
    private lateinit var reviewList: ArrayList<Reviews>
    private var db = Firebase.firestore
    private var totalRating: Int = 0

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
        binding = FragmentGymLocationDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(GymLocationDetailViewModel::class.java)
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_gym_location, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            gymDetail = it.getSerializable(ARG_GYM_LOCATION) as GymLocation
            totalRating = it.getInt(ARG_TOTAL_RATING, 0)
        }

        recyclerView = binding.rvReviews

        reviewList = arrayListOf()
        var adapter = this.view?.let { ReviewAdapter(reviewList) }
        recyclerView.adapter = adapter

        var manager = LinearLayoutManager(this.requireContext())
        recyclerView.layoutManager = manager

//        val imageUrl = gymDetail.pictures[0] // Replace this with the actual URL or resource of your image
//        Glide.with(requireContext())
//            .load(imageUrl)
//            .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
//            .into(binding.gymLocationImg)

        val imageSlider = binding.gymLocationImg
        val imageList = ArrayList<SlideModel>()

        for (picture in gymDetail.pictures) {
            // Assuming picture is a URL string
            imageList.add(SlideModel(picture))
        }
        imageSlider.setImageList(imageList)

        binding.gymNameLbl.text = gymDetail.name
        binding.gymAddressLbl.text = gymDetail.address
        binding.gymRatingLbl.text = "${totalRating}.0"

        val rating = totalRating
        when (rating) {
            1 -> {
                // Set the color of the first star to yellow
                binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            }
            2 -> {
                // Set the color of the first two stars to yellow
                binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            }
            3 -> {
                // Set the color of the first two stars to yellow
                binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            }
            4 -> {
                // Set the color of the first two stars to yellow
                binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            }
            5 -> {
                // Set the color of the first two stars to yellow
                binding.star1.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star2.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star3.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star4.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
                binding.star5.setColorFilter(ContextCompat.getColor(requireContext(), R.color.custom_yellow))
            }
        }

        binding.reviewBtn.setOnClickListener {
            val addReviewFragment = AddReviewFragment.newInstance(gymDetail)

            val fragmentManager = requireActivity().supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, addReviewFragment)
            fragmentTransaction.commit()
        }

        val gymLocationRepository = GymLocationRepository()

        gymLocationRepository.fetchAllReviewByGymId(gymDetail.uid) { reviewList ->
            activity?.runOnUiThread {
                val adapter = ReviewAdapter(reviewList as ArrayList<Reviews>)
                recyclerView.adapter = adapter
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
         * @return A new instance of fragment GymLocationDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        private const val ARG_GYM_LOCATION = "gymLocation"
        private const val ARG_TOTAL_RATING = "totalRating"

        @JvmStatic
        fun newInstance(gymLocation: GymLocation, totalRating: Int): GymLocationDetailFragment {
            val fragment = GymLocationDetailFragment()
            val args = Bundle()
            args.putSerializable(ARG_GYM_LOCATION, gymLocation)
            args.putInt(ARG_TOTAL_RATING, totalRating)

            fragment.arguments = args
            return fragment
        }
    }
}