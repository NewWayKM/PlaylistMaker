package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val tracks: ArrayList<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)

        val holder = TrackViewHolder(view)

        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition

            if (position != RecyclerView.NO_POSITION) {
                onTrackClick(tracks[position])
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        notifyDataSetChanged()
    }
}