package ru.labone.addnote.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.labone.R
import ru.labone.FileData
import ru.labone.loadWithBackgroundBlur

class ImageAdapter(
    private val files: List<FileData>,
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var listener: ((pos: Int, id: String) -> Unit)? = null

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val imageView2: FrameLayout = itemView.findViewById(R.id.image2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val file = files[position]
        holder.imageView.loadWithBackgroundBlur(file.uri)
        holder.imageView2.setOnClickListener {
            listener?.invoke(position, file.id)
        }
    }

    override fun getItemCount(): Int = files.size
}
