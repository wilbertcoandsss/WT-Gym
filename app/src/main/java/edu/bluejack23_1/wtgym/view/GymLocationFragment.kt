package edu.bluejack23_1.wtgym.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.adapter.ExerciseAdapter
import edu.bluejack23_1.wtgym.adapter.GymLocationAdapter
import edu.bluejack23_1.wtgym.databinding.FragmentExercisesBinding
import edu.bluejack23_1.wtgym.databinding.FragmentGymLocationBinding
import edu.bluejack23_1.wtgym.databinding.FragmentHomeBinding
import edu.bluejack23_1.wtgym.model.Exercise
import edu.bluejack23_1.wtgym.model.ExerciseRepository
import edu.bluejack23_1.wtgym.model.GymLocation
import edu.bluejack23_1.wtgym.model.GymLocationRepository
import edu.bluejack23_1.wtgym.viewmodel.ExercisesViewModel
import edu.bluejack23_1.wtgym.viewmodel.GymLocationViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GymLocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GymLocationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentGymLocationBinding
    private lateinit var viewModel: GymLocationViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var gymLocationList: ArrayList<GymLocation>
    private var db = Firebase.firestore

    private var currentItems: Int = 0
    private var scrollOutItems: Int = 0
    private var totalItems: Int = 0
    private var loading: Boolean = false
    private var isScrolling: Boolean = false

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
        binding = FragmentGymLocationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(GymLocationViewModel::class.java)
        // Inflate the layout for this fragment
        //        return inflater.inflate(R.layout.fragment_gym_location, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.rvGymLocation
        //        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())

        gymLocationList = arrayListOf()
        var adapter = this.view?.let { GymLocationAdapter(gymLocationList) }
        recyclerView.adapter = adapter

        val pixelsToScroll = 200
        recyclerView.scrollBy(0, pixelsToScroll)

//            val gymLocationRepository = GymLocationRepository()
//
//            gymLocationRepository.fetchAllGymLocations { gymLocationList ->
//                // Update the UI with the retrieved exerciseList
//                activity?.runOnUiThread {
//                    val adapter = GymLocationAdapter(gymLocationList as ArrayList<GymLocation>)
//                    adapter.setOnItemClickListener { selectedGymLocation ->
//                        Log.d("GymLocationClicked", "Gym location clicked: $selectedGymLocation")
//                        // Handle the click event, for example, navigate to the detail fragment
//                        val gymDetailFragment = GymLocationDetailFragment.newInstance(selectedGymLocation)
//
//                        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//                        transaction.replace(R.id.frameLayout, gymDetailFragment)
//                        transaction.commit()
//                    }
//                    recyclerView.adapter = adapter
//                }
//            }

        var manager = LinearLayoutManager(this.requireContext())
        recyclerView.layoutManager = manager

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                currentItems = manager.childCount
                totalItems = manager.itemCount
                scrollOutItems = manager.findFirstVisibleItemPosition()

                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    fetchMoreGymLocation()
                }
            }
        })

        var bottomSheetDialog: BottomSheetDialog

        binding.filterBtn1.setOnClickListener {
            val bottomView = layoutInflater.inflate(R.layout.bottomsheet_fragment, null)

            var allFiltering = bottomView.findViewById<RadioButton>(R.id.filtering1)
            var jakartaFiltering = bottomView.findViewById<RadioButton>(R.id.filtering2)
            var bandungFiltering = bottomView.findViewById<RadioButton>(R.id.filtering3)
            var semarangFiltering = bottomView.findViewById<RadioButton>(R.id.filtering4)

            allFiltering.text = "All"
            jakartaFiltering.text = "Jakarta"
            bandungFiltering.text = "Bandung"
            semarangFiltering.text = "Semarang"

            bottomView.findViewById<RadioButton>(R.id.filtering5).visibility = View.GONE
            bottomView.findViewById<RadioButton>(R.id.filtering6).visibility = View.GONE

            bottomView.findViewById<TextView>(R.id.titleLbl).text = "Sort By City"

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            val gymListAll: MutableList<GymLocation> = mutableListOf()

            allFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    loadGymLocation(4)
                }
            }

            jakartaFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {

                    val gymLocationRepository = GymLocationRepository()

                    // Call fetchAllExercises with a callback
                    gymLocationRepository.fetchAllGymLocations { gymLocations ->
                        // Update exerciseListAll with the fetched exercises
                        gymListAll.clear()
                        gymListAll.addAll(gymLocations)

                        // Now you can use exerciseListAll for filtering
                        val filteredGymLocation = gymListAll.filter { gymLocation ->
                            gymLocation.city.equals("Jakarta", ignoreCase = false)
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            GymLocationAdapter(
                                filteredGymLocation as ArrayList<GymLocation>
                            )
                        }
                    }
                }
            }

            bandungFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {

                    val gymLocationRepository = GymLocationRepository()

                    // Call fetchAllExercises with a callback
                    gymLocationRepository.fetchAllGymLocations { gymLocations ->
                        // Update exerciseListAll with the fetched exercises
                        gymListAll.clear()
                        gymListAll.addAll(gymLocations)

                        // Now you can use exerciseListAll for filtering
                        val filteredGymLocation = gymListAll.filter { gymLocation ->
                            gymLocation.city.equals("Bandung", ignoreCase = false)
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            GymLocationAdapter(
                                filteredGymLocation as ArrayList<GymLocation>
                            )
                        }
                    }
                }
            }

            semarangFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {

                    val gymLocationRepository = GymLocationRepository()

                    // Call fetchAllExercises with a callback
                    gymLocationRepository.fetchAllGymLocations { gymLocations ->
                        // Update exerciseListAll with the fetched exercises
                        gymListAll.clear()
                        gymListAll.addAll(gymLocations)

                        // Now you can use exerciseListAll for filtering
                        val filteredGymLocation = gymListAll.filter { gymLocation ->
                            gymLocation.city.equals("Semarang", ignoreCase = false)
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            GymLocationAdapter(
                                filteredGymLocation as ArrayList<GymLocation>
                            )
                        }
                    }
                }
            }

        }
        loadGymLocation(4)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GymLocationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GymLocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun loadGymLocation(limit: Int) {
        val gymLocationRepository = GymLocationRepository()

        gymLocationRepository.fetchGymLocationWithLimit(limit) { fetchedGymLocationList ->
            gymLocationList.clear()
            gymLocationList.addAll(fetchedGymLocationList)
            recyclerView.adapter = this.view?.let { GymLocationAdapter(gymLocationList) }
        }
    }

    private fun fetchMoreGymLocation() {
        binding.progBar.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed({
            val newLimit = gymLocationList.size + 1
            val gymLocationRepository = GymLocationRepository()
            gymLocationRepository.fetchGymLocationWithLimit(
                newLimit
            ) { fetchGymLocationWithLimit ->
                gymLocationList.clear()
                gymLocationList.addAll(fetchGymLocationWithLimit)
                recyclerView.adapter?.notifyDataSetChanged()
                loading = false
            }
            binding.progBar.visibility = View.INVISIBLE
        }, 800) // 1500 milliseconds (1.5 seconds)

    }
}