package ru.labone.addnews.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.labone.R
import ru.labone.loadWithRoundedCorners

class ImageAdapter(private val images: List<Uri>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var listener: ((pos: Int) -> Unit)? = null

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val imageView2: FrameLayout = itemView.findViewById(R.id.image2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.loadWithRoundedCorners(images[position])
        holder.imageView2.setOnClickListener {
            listener?.invoke(position)
        }
    }

    override fun getItemCount(): Int = images.size

}
