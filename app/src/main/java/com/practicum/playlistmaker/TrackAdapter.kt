package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// 1. Адаптер списка треков
class TrackAdapter(
    tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    // 1.1. Внутренний список треков
    private val tracks = ArrayList(tracks)

    // 2. Создание элемента списка
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        // 2.1. Создание интерфейса элемента
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_track, parent, false)

        val holder = TrackViewHolder(view)

        // 2.2. Обработка нажатия на трек
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition

            // 2.3. Проверка актуальности позиции
            if (position != RecyclerView.NO_POSITION) {
                onTrackClick(tracks[position])
            }
        }

        return holder
    }

    // 3. Отображение данных трека
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    // 4. Получение количества треков
    override fun getItemCount(): Int {
        return tracks.size
    }

    // 5. Обновление списка треков
    fun updateTracks(newTracks: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracks)
        // 5.1. Уведомление RecyclerView об изменениях
        notifyDataSetChanged()
    }
}