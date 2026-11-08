package com.practicum.playlistmaker.data.repository

import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.data.dto.TrackSearchResponseDto
import com.practicum.playlistmaker.data.mapper.TrackMapper
import com.practicum.playlistmaker.data.network.ITunesApiService
import com.practicum.playlistmaker.domain.api.Cancellable
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.repository.TracksRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.atomic.AtomicBoolean

// 1. Реализация поиска треков через iTunes API
class TracksRepositoryImpl(
    private val api: ITunesApiService,
    private val callbackHandler: Handler = Handler(Looper.getMainLooper())
) : TracksRepository {

    // 2. Выполнение асинхронного поиска
    override fun search(
        query: String,
        consumer: (SearchResult) -> Unit
    ): Cancellable {

        // 2.1. Создание сетевого запроса
        val call = api.searchTracks(query)
        val cancelled = AtomicBoolean(false)

        // 2.2. Передача результата в главный поток
        fun deliver(result: SearchResult) {
            callbackHandler.post {
                if (!cancelled.get() && !call.isCanceled) {
                    consumer(result)
                }
            }
        }

        // 2.3. Асинхронное выполнение запроса
        call.enqueue(object : Callback<TrackSearchResponseDto> {

            // 2.4. Обработка ответа сервера
            override fun onResponse(
                call: Call<TrackSearchResponseDto>,
                response: Response<TrackSearchResponseDto>
            ) {
                if (cancelled.get() || call.isCanceled) {
                    return
                }

                val body = response.body()

                if (response.isSuccessful && body != null) {
                    val tracks = body.results
                        .orEmpty()
                        .filterNotNull()
                        .map(TrackMapper::toDomain)

                    deliver(SearchResult.Success(tracks))
                } else {
                    deliver(SearchResult.Error)
                }
            }

            // 2.5. Обработка ошибки сети
            override fun onFailure(
                call: Call<TrackSearchResponseDto>,
                error: Throwable
            ) {
                if (cancelled.get() || call.isCanceled) {
                    return
                }

                deliver(SearchResult.Error)
            }
        })

        // 3. Возможность отмены запроса
        return Cancellable {
            cancelled.set(true)
            call.cancel()
        }
    }
}