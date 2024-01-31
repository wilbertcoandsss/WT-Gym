package edu.bluejack23_1.wtgym.view

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.wtgym.adapter.ExerciseAdapter
import edu.bluejack23_1.wtgym.databinding.FragmentFavoriteBinding
import edu.bluejack23_1.wtgym.model.Exercise
import edu.bluejack23_1.wtgym.model.ExerciseRepository
import edu.bluejack23_1.wtgym.model.UserRepository
import edu.bluejack23_1.wtgym.viewmodel.FavoriteViewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExerciseAdapter

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var favExerciseList: ArrayList<Exercise>

    private var userId: String? = null

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
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRepo1 = UserRepository(requireContext())
        val role = userRepo1.getCurrentUser()?.userRole

        recyclerView = binding.rvFavExercises

        favExerciseList = arrayListOf()

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
                    fetchMoreFavExercises()
                }
            }
        })

        val searchBar = binding.searchBar

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

        val exercisesRepository = ExerciseRepository()
        val userRepo = UserRepository(this.requireContext())
        userId = userRepo.getCurrentUser()?.userId.toString()
        exercisesRepository.getUserFavorites(userId!!, 3) { favoriteExercises ->
            favExerciseList.clear()
            favExerciseList.addAll(favoriteExercises)
            recyclerView.adapter = this.view?.let {ExerciseAdapter(favExerciseList, it, userRepo.getCurrentUser()?.userRole)}
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun filterExercises(query: String?) {
        val filteredList = if (!query.isNullOrBlank()) {
            favExerciseList.filter { exercise ->
                exercise.name.contains(query, ignoreCase = true)
            }
        } else {
            favExerciseList // Return the original list when the query is empty
        }

        val userRepo1 = UserRepository(requireContext())
        val role = userRepo1.getCurrentUser()?.userRole
        recyclerView.adapter =
            this.view?.let { ExerciseAdapter(filteredList as ArrayList<Exercise>, it, role ) }
    }

    private fun fetchMoreFavExercises() {
        binding.progBar.visibility = View.VISIBLE
        val handler = Handler()
        handler.postDelayed({
            val newLimit = favExerciseList.size + 1
            val exercisesRepository = ExerciseRepository()
            userId?.let {
                exercisesRepository.getUserFavorites(
                    it,
                    newLimit
                ) { fetchedExerciseList ->
                    favExerciseList.clear()
                    favExerciseList.addAll(fetchedExerciseList)
                    recyclerView.adapter?.notifyDataSetChanged()
                    loading = false
                }
            }
            binding.progBar.visibility = View.INVISIBLE
        }, 800) // 1500 milliseconds (1.5 seconds)


    }
}