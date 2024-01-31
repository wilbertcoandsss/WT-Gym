package edu.bluejack23_1.wtgym.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import edu.bluejack23_1.wtgym.R
import edu.bluejack23_1.wtgym.model.Reviews
import edu.bluejack23_1.wtgym.view.GymLocationDetailFragment

class ReviewAdapter(
    private val reviewList: ArrayList<Reviews>
) : RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {
    private var itemClickListener: ((Reviews) -> Unit)? = null

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserName: TextView = itemView.findViewById(R.id.userName)
        val tvUserReview: TextView = itemView.findViewById(R.id.userReview)
        val tvStar1: ImageView = itemView.findViewById(R.id.star1)
        val tvStar2: ImageView = itemView.findViewById(R.id.star2)
        val tvStar3: ImageView = itemView.findViewById(R.id.star3)
        val tvStar4: ImageView = itemView.findViewById(R.id.star4)
        val tvStar5: ImageView = itemView.findViewById(R.id.star5)
        val tvProfilePic: ImageView = itemView.findViewById(R.id.userImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_review, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    fun setOnItemClickListener(listener: (Reviews) -> Unit) {
        itemClickListener = listener
    }

    override fun onBindViewHolder(holder: ReviewAdapter.MyViewHolder, position: Int) {
        holder.tvUserName.text = reviewList[position].name
        holder.tvUserReview.text = reviewList[position].review

        val imageUrl = reviewList[position].profilePic
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.circular_image)
            .circleCrop()
            .into(holder.itemView.findViewById<ImageView>(R.id.userImg))

        val rating = reviewList[position].rating

        when (rating) {
            1 -> {
                holder.tvStar1.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            2 -> {
                holder.tvStar1.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar2.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            3 -> {
                holder.tvStar1.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar2.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar3.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            4 -> {
                holder.tvStar1.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar2.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar3.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar4.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
            5 -> {
                holder.tvStar1.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar2.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar3.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar4.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                holder.tvStar5.setColorFilter(
                    holder.itemView.context.resources.getColor(R.color.custom_yellow),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }

    }

}