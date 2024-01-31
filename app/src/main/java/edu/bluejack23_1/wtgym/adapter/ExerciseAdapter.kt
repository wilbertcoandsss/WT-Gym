package edu.bluejack23_1.wtgym.adapter

import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.model.Exercise
import edu.bluejack23_1.wtgym.model.ExerciseRepository
import edu.bluejack23_1.wtgym.view.EditExercisesFragment
import edu.bluejack23_1.wtgym.view.ExercisesDetailFragment
import edu.bluejack23_1.wtgym.view.ExercisesFragment


class ExerciseAdapter(
    private val exerciseList: MutableList<Exercise>,
    private val fragmentView: View,
    private val userRole: String?
) :
    RecyclerView.Adapter<ExerciseAdapter.MyViewHolder>() {

    private var onItemClickListener: ((Exercise) -> Unit)? = null

    // Function to set the click listener
    fun setOnItemClickListener(listener: (Exercise) -> Unit) {
        onItemClickListener = listener
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvExerciseName: TextView = itemView.findViewById(R.id.exercisesName)
        val exerciseCover: ImageView = itemView.findViewById(R.id.exercisesImg)
        val mainContainer: ConstraintLayout = itemView.findViewById(R.id.main_container)
        val editBtn: ImageView = itemView.findViewById(R.id.editBtn)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteBtn)

        val rootView: View = itemView.rootView

        init {
            itemView.setOnClickListener { v: View ->
                Log.d("MASUK GK KLIK", "KLICK")
                val position: Int = adapterPosition
                Toast.makeText(
                    itemView.context,
                    "You clicked this one ${position + 1}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val exercise = exerciseList[position]

        holder.tvExerciseName.text = exerciseList[position].name

        val imageUrl = exercise.cover // Replace this with the actual URL or resource of your image

        Log.d("Ada ga", imageUrl.toString())
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.circular_image) // Replace this with your placeholder drawable
            .into(holder.exerciseCover)

        if (userRole == "admin") {
            holder.editBtn.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.VISIBLE
        } else {
            holder.editBtn.visibility = View.GONE
            holder.deleteBtn.visibility = View.GONE
        }

        holder.editBtn.setOnClickListener {
            val exerciseUpdateFragment = EditExercisesFragment.newInstance(exercise)
            val transaction =
                (fragmentView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, exerciseUpdateFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        var bottomSheetDialog: BottomSheetDialog

        holder.deleteBtn.setOnClickListener {
            val dialog = android.app.AlertDialog.Builder(holder.itemView.context)

            dialog.setTitle("Delete Exercises")
            dialog.setMessage("Are you sure want to delete this exercises ?")

            dialog.setPositiveButton("Delete") { _, _ ->
                deleteExercise(position)
                val snackbar = Snackbar.make(
                    holder.rootView,
                    "Successfully Deleted Exercises",
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

                Handler().postDelayed({
                    if (exercise.isGym) {
                        val exercisesFragment = ExercisesFragment.newInstance("gym")
                        val transaction =
                            (fragmentView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frameLayout, exercisesFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    } else {
                        val exercisesFragment = ExercisesFragment.newInstance("home")

                        val transaction =
                            (fragmentView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.frameLayout, exercisesFragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }, 1050) // 3000 milliseconds (3 seconds)

            }

            dialog.setNeutralButton("Cancel") { _, _ ->

            }

            dialog.show()
        }

        holder.mainContainer.setOnClickListener {
            val exerciseDetailFragment = ExercisesDetailFragment.newInstance(exercise)
            val transaction =
                (fragmentView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, exerciseDetailFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    fun deleteExercise(position: Int) {
        val exercise = exerciseList[position]
        val exerciseId = exercise.uid

        val exerRepo = ExerciseRepository()
        exerRepo.deleteExercise(exerciseId,
            onSuccess = {
                exerciseList.removeAt(position)
                notifyItemRemoved(position)
            },
            onFailure = { e ->
                // Handle the deletion failure
            }
        )
    }

//    fun getDataItem(position: Int): YourDataItem {
//        return exerciseList[position]
//    }
//
//    fun recoverSwipedItem(item: Exercise, position: Int) {
//        exerciseList.add(position, item)
//        exerciseList.
//    }

    interface MyClickListener {
        fun onClick(position: Int)
    }

}