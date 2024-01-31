package edu.bluejack23_1.wtgym.view

//import android.R
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.adapter.ExerciseAdapter
import edu.bluejack23_1.wtgym.databinding.FragmentExercisesBinding
import edu.bluejack23_1.wtgym.model.Exercise
import edu.bluejack23_1.wtgym.model.ExerciseRepository
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.ExercisesViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExercisesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExercisesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var binding: FragmentExercisesBinding
    private lateinit var viewModel: ExercisesViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseList: ArrayList<Exercise>
    private var db = Firebase.firestore

    val data = this.arguments?.getString("key")

    val exerciseListNew: MutableList<String> = ArrayList()

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
        binding = FragmentExercisesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(ExercisesViewModel::class.java)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val userRepo = UserRepository(requireContext())
        val role = userRepo.getCurrentUser()?.userRole

        val menuItem = layoutInflater.inflate(R.layout.list_item, null)

        val editBtn = menuItem.findViewById<ImageView>(R.id.editBtn)
        val deleteBtn = menuItem.findViewById<ImageView>(R.id.deleteBtn)


        recyclerView = binding.rvExercises
        exerciseList = arrayListOf()
        var adapter = this.view?.let { ExerciseAdapter(exerciseList, it, role) }

        recyclerView.adapter = adapter

//        val itemTouchHelper = adapter?.let { SwipeToEditDeleteCallback(it) }
//            ?.let { ItemTouchHelper(it) }
//        if (itemTouchHelper != null) {
//            itemTouchHelper.attachToRecyclerView(recyclerView)
//        }

        // Set the number of pixels you want to scroll
        val pixelsToScroll = 200  // Adjust this value as needed

// Programmatically scroll the RecyclerView by the specified number of pixels
        recyclerView.scrollBy(0, pixelsToScroll)


        if (adapter != null) {
            adapter.setOnItemClickListener { exercise ->
                // Handle the click event and navigate to ExercisesDetailFragment
                Log.d("MASUK GK", "AWIAKWOK")
                val exerciseDetailFragment = ExercisesDetailFragment.newInstance(exercise)
                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frameLayout, exerciseDetailFragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }


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
                    fetchMoreExercises()
                }
            }
        })

        var bottomSheetDialog: BottomSheetDialog

        val searchBar = binding.searchBar

        if (this.arguments?.getString("key").toString().equals("home")) {
            binding.exercisesTypeLbl.text = "Home Exercises"
            binding.filterBtn3.visibility = View.GONE
            binding.imageView7.visibility = View.GONE
            binding.lblTag.text = "Home"

        } else if (this.arguments?.getString("key").toString().equals("gym")) {
            binding.exercisesTypeLbl.text = "Gym Exercises"
            binding.lblTag.text = "Gym"
        }

        if (role == "admin"){
            binding.insertBtn.visibility = View.VISIBLE
        }
        else{
            binding.insertBtn.visibility = View.GONE
        }

        binding.insertBtn.setOnClickListener{
            var string: String? = null

            if (this.arguments?.getString("key").toString().equals("home")) {
                string = "home"
            }
            else{
                string = "gym"
            }
            val insertExercisesFragment = InsertExerciseFragment.newInstance(string)
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, insertExercisesFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // Type
        binding.filterBtn1.setOnClickListener {
            Log.d("1", "INI PENCET YG FILTER PERTAMA")
            val bottomView = layoutInflater.inflate(R.layout.bottomsheet_fragment, null)

            var allFiltering = bottomView.findViewById<RadioButton>(R.id.filtering1)
            var strengthFiltering = bottomView.findViewById<RadioButton>(R.id.filtering2)
            var cardioFiltering = bottomView.findViewById<RadioButton>(R.id.filtering3)
            var stretchFiltering = bottomView.findViewById<RadioButton>(R.id.filtering4)

            allFiltering.text = "All"
            strengthFiltering.text = "Strength"
            cardioFiltering.text = "Cardio"
            stretchFiltering.text = "Stretch"

            bottomView.findViewById<RadioButton>(R.id.filtering5).visibility = View.GONE
            bottomView.findViewById<RadioButton>(R.id.filtering6).visibility = View.GONE

            bottomView.findViewById<TextView>(R.id.titleLbl).text = "Sort By Type"

            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            val exerciseListAll: MutableList<Exercise> = mutableListOf()

            allFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = ""
                    loadExercises(3)
                }
            }

            strengthFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Type: Strength"

                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.type.equals("Cardio", ignoreCase = false)
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }


            cardioFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Type: Cardio"

                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.type.equals("Cardio", ignoreCase = false)
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }

            stretchFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Type: Stretch"

                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.type.equals("Stretching", ignoreCase = false)
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }

        }

        // Muscle
        binding.filterBtn2.setOnClickListener {
            val bottomView = layoutInflater.inflate(R.layout.bottomsheet_fragment, null)

            var allFiltering = bottomView.findViewById<RadioButton>(R.id.filtering1)
            var chestFiltering = bottomView.findViewById<RadioButton>(R.id.filtering2)
            var armsFiltering = bottomView.findViewById<RadioButton>(R.id.filtering3)
            var backFiltering = bottomView.findViewById<RadioButton>(R.id.filtering4)
            var legFiltering = bottomView.findViewById<RadioButton>(R.id.filtering5)
            var absFiltering = bottomView.findViewById<RadioButton>(R.id.filtering6)
            val exerciseListAll: MutableList<Exercise> = mutableListOf()
            bottomSheetDialog = BottomSheetDialog(this.requireContext())
            bottomSheetDialog.setContentView(bottomView)
            bottomSheetDialog.show()

            chestFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Muscles: Chest"
                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.muscles.any { muscle ->
                                muscle.contains(
                                    "Chest",
                                    ignoreCase = true
                                )
                            }
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }

            armsFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Muscles: Arms"
                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.muscles.any { muscle ->
                                muscle.contains(
                                    "Arms",
                                    ignoreCase = true
                                )
                            }
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }

            backFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Muscles: Back"
                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.muscles.any { muscle ->
                                muscle.contains(
                                    "Back",
                                    ignoreCase = true
                                )
                            }
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }

            legFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Muscles: Legs"
                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.muscles.any { muscle ->
                                muscle.contains(
                                    "Legs",
                                    ignoreCase = true
                                )
                            }
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }

            absFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = "Sort By Muscles: Abdominal"
                    val exerciseRepository = ExerciseRepository()

                    // Call fetchAllExercises with a callback
                    exerciseRepository.fetchAllExercises { exercises ->
                        // Update exerciseListAll with the fetched exercises
                        exerciseListAll.clear()
                        exerciseListAll.addAll(exercises)

                        // Now you can use exerciseListAll for filtering
                        val filteredExercises = exerciseListAll.filter { exercise ->
                            exercise.muscles.any { muscle ->
                                muscle.contains(
                                    "Abdominal",
                                    ignoreCase = true
                                )
                            }
                        }

                        recyclerView.adapter = this.view?.let { it1 ->
                            ExerciseAdapter(
                                filteredExercises as ArrayList<Exercise>,
                                it1,
                                role
                            )
                        }
                    }
                }
            }
            allFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                if (isChecked) {
                    binding.sortTxt.text = ""
                    loadExercises(3)
                }
            }

            // Equipment
            binding.filterBtn3.setOnClickListener {
                val bottomView = layoutInflater.inflate(R.layout.bottomsheet_fragment, null)

                var allFiltering = bottomView.findViewById<RadioButton>(R.id.filtering1)
                var dumbellFiltering = bottomView.findViewById<RadioButton>(R.id.filtering2)
                var bodyWeightFiltering = bottomView.findViewById<RadioButton>(R.id.filtering3)
                var machineFiltering = bottomView.findViewById<RadioButton>(R.id.filtering4)
                val exerciseListAll: MutableList<Exercise> = mutableListOf()
                allFiltering.text = "All"
                dumbellFiltering.text = "Dumbell"
                bodyWeightFiltering.text = "Body Weight"
                machineFiltering.text = "Leverage Machine"

                bottomView.findViewById<RadioButton>(R.id.filtering5).visibility = View.GONE
                bottomView.findViewById<RadioButton>(R.id.filtering6).visibility = View.GONE
                bottomView.findViewById<TextView>(R.id.titleLbl).text = "Sort By Equipment"

                bottomSheetDialog = BottomSheetDialog(this.requireContext())
                bottomSheetDialog.setContentView(bottomView)
                bottomSheetDialog.show()

                allFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                    if (isChecked) {
                        loadExercises(3)
                        binding.sortTxt.text = ""
                    }
                }

                dumbellFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                    if (isChecked) {
                        binding.sortTxt.text = "Sort By Equipment: Dumbell"
                        val exerciseRepository = ExerciseRepository()

                        // Call fetchAllExercises with a callback
                        exerciseRepository.fetchAllExercises { exercises ->
                            // Update exerciseListAll with the fetched exercises
                            exerciseListAll.clear()
                            exerciseListAll.addAll(exercises)

                            // Now you can use exerciseListAll for filtering
                            val filteredExercises = exerciseListAll.filter { exercise ->
                                exercise.equipment.equals("Dumbell", ignoreCase = false)
                            }

                            recyclerView.adapter = this.view?.let { it1 ->
                                ExerciseAdapter(
                                    filteredExercises as ArrayList<Exercise>,
                                    it1,
                                    role
                                )
                            }
                        }
                    }
                }

                bodyWeightFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                    if (isChecked) {
                        binding.sortTxt.text = "Sort By Equipment: Body Weight"
                        val exerciseRepository = ExerciseRepository()

                        // Call fetchAllExercises with a callback
                        exerciseRepository.fetchAllExercises { exercises ->
                            // Update exerciseListAll with the fetched exercises
                            exerciseListAll.clear()
                            exerciseListAll.addAll(exercises)

                            // Now you can use exerciseListAll for filtering
                            val filteredExercises = exerciseListAll.filter { exercise ->
                                exercise.equipment.equals("Body Weight", ignoreCase = false)
                            }

                            recyclerView.adapter = this.view?.let { it1 ->
                                ExerciseAdapter(
                                    filteredExercises as ArrayList<Exercise>,
                                    it1,
                                    role
                                )
                            }
                        }
                    }
                }

                machineFiltering.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
                    if (isChecked) {
                        binding.sortTxt.text = "Sort By Equipment: Leverage Machine"
                        val exerciseRepository = ExerciseRepository()

                        // Call fetchAllExercises with a callback
                        exerciseRepository.fetchAllExercises { exercises ->
                            // Update exerciseListAll with the fetched exercises
                            exerciseListAll.clear()
                            exerciseListAll.addAll(exercises)

                            // Now you can use exerciseListAll for filtering
                            val filteredExercises = exerciseListAll.filter { exercise ->
                                exercise.equipment.equals("Leverage Machine", ignoreCase = false)
                            }

                            recyclerView.adapter = this.view?.let { it1 ->
                                ExerciseAdapter(
                                    filteredExercises as ArrayList<Exercise>,
                                    it1,
                                    role
                                )
                            }
                        }
                    }
                }

            }
        }

        searchBar.clearFocus()
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle submission if needed
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterExercises(newText)
                return true
            }
        })


        loadExercises(3)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExercisesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(data: String): ExercisesFragment {
            val fragment = ExercisesFragment()
            val args = Bundle()
            args.putString("key", data)
            fragment.arguments = args
            return fragment
        }
    }

    private fun filterExercises(query: String?) {
        val filteredList = if (!query.isNullOrBlank()) {
            exerciseList.filter { exercise ->
                exercise.name.contains(query, ignoreCase = true)
            }
        } else {
            exerciseList // Return the original list when the query is empty
        }
        val userRepo = UserRepository(requireContext())
        val role = userRepo.getCurrentUser()?.userRole
        recyclerView.adapter =
            this.view?.let { ExerciseAdapter(filteredList as ArrayList<Exercise>, it, role) }
    }

    private fun loadExercises(limit: Int) {
        val exercisesRepository = ExerciseRepository()
        val isGymFilter = this.arguments?.getString("key") == "gym"

        val userRepo = UserRepository(requireContext())
        val role = userRepo.getCurrentUser()?.userRole
        exercisesRepository.fetchExercisesWithLimit(limit, isGymFilter) { fetchedExerciseList ->
            exerciseList.clear()
            exerciseList.addAll(fetchedExerciseList)
            recyclerView.adapter = this.view?.let { ExerciseAdapter(exerciseList, it, role) }
        }
    }

    private fun fetchMoreExercises() {
        binding.progBar.visibility = View.VISIBLE
        val handler = Handler()
        val isGymFilter = this.arguments?.getString("key") == "gym"
        handler.postDelayed({
            val newLimit = exerciseList.size + 1
            val exercisesRepository = ExerciseRepository()
            exercisesRepository.fetchExercisesWithLimit(
                newLimit,
                isGymFilter
            ) { fetchedExerciseList ->
                exerciseList.clear()
                exerciseList.addAll(fetchedExerciseList)
                recyclerView.adapter?.notifyDataSetChanged()
                loading = false
            }
            binding.progBar.visibility = View.INVISIBLE
        }, 800) // 1500 milliseconds (1.5 seconds)


    }
}

private fun SearchView.setOnQueryTextListener(onQueryTextListener: SearchView.OnQueryTextListener) {
    setOnQueryTextListener(onQueryTextListener)
}
