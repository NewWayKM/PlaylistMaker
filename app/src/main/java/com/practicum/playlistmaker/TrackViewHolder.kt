package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackCover: ImageView = itemView.findViewById(R.id.trackCover)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(track: Track) {
        trackName.text = track.trackName
            ?.takeIf { it.isNotBlank() }
            ?: itemView.context.getString(R.string.track_unknown_name)

        artistName.text = track.artistName
            ?.takeIf { it.isNotBlank() }
            ?: itemView.context.getString(R.string.track_unknown_artist)

        trackTime.text = TrackDurationFormatter.format(track.trackTimeMillis)

        val cornerRadius = itemView.resources.getDimensionPixelSize(
            R.dimen.track_cover_corner_radius
        )

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_track_placeholder)
                    .error(R.drawable.ic_track_placeholder)
                    .fallback(R.drawable.ic_track_placeholder)
                    .transform(CenterCrop(), RoundedCorners(cornerRadius))
            )
            .into(trackCover)

    }
}