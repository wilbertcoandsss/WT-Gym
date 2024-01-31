package edu.bluejack23_1.wtgym.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.model.GymLocation
import com.bumptech.glide.Glide
import edu.bluejack23_1.wtgym.view.ExercisesDetailFragment
import edu.bluejack23_1.wtgym.view.GymLocationDetailFragment

class GymLocationAdapter(
    private val gymLocationList: ArrayList<GymLocation>
) :
    RecyclerView.Adapter<GymLocationAdapter.MyViewHolder>() {
    private var itemClickListener: ((GymLocation) -> Unit)? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvGymLocationName: TextView = itemView.findViewById(R.id.gymName)
        val tvGymLocationAddress: TextView = itemView.findViewById(R.id.gymAddress)
        val tvGymLocationRating: TextView = itemView.findViewById(R.id.gymRating)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_gym, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return gymLocationList.size
    }

    fun setOnItemClickListener(listener: (GymLocation) -> Unit) {
        itemClickListener = listener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvGymLocationName.text = gymLocationList[position].name
        holder.tvGymLocationAddress.text = gymLocationList[position].address

        // Accessing the rating
        val reviewsList = gymLocationList[position].reviews
        var totalRating = 0
        for (review in reviewsList) {
            totalRating += review.rating
        }
        val averageRating = if (reviewsList.isNotEmpty()) (totalRating / reviewsList.size).toInt() else 0
        holder.tvGymLocationRating.text = "$averageRating / 5"

        val imageUrl = gymLocationList[position].pictures[0]
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.circular_image)
            .into(holder.itemView.findViewById<ImageView>(R.id.gymLocationImg))

        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(gymLocationList[position])
            Log.d("MASUKKKKK", gymLocationList[position].name)

            val selectedGymLocation = gymLocationList[position]
            val gymDetailFragment = GymLocationDetailFragment.newInstance(selectedGymLocation, gymLocationList[position].totalRating)

            val fragmentManager = (holder.itemView.context as AppCompatActivity).supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.frameLayout, gymDetailFragment)
            fragmentTransaction.commit()
        }

    }

}