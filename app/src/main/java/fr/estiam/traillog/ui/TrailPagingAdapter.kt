package fr.estiam.traillog.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import fr.estiam.traillog.databinding.ItemTrailBinding
import fr.estiam.traillog.model.*

class TrailPagingAdapter(private val onClick: (Trail) -> Unit) :
    PagingDataAdapter<Trail, TrailPagingAdapter.Holder>(DIFF) {
    var unit: DistanceUnit = DistanceUnit.KM
        set(value) { field = value; notifyItemRangeChanged(0, itemCount) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(ItemTrailBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        getItem(position)?.let { holder.bind(it, unit, onClick) }
    }
    class Holder(private val binding: ItemTrailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(trail: Trail, unit: DistanceUnit, onClick: (Trail) -> Unit) {
            binding.name.text = trail.name
            binding.region.text = trail.region
            binding.distance.text = TrailFormatter.distance(trail.distanceKm, unit)
            binding.difficulty.text = trail.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }
            binding.card.contentDescription = "${trail.name}, ${binding.distance.text}, ${trail.region}"
            binding.root.setOnClickListener { onClick(trail) }
        }
    }
    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Trail>() {
            override fun areItemsTheSame(oldItem: Trail, newItem: Trail) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Trail, newItem: Trail) = oldItem == newItem
        }
    }
}
