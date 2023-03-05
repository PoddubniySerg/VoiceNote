package com.test.voicenote.presentation.ui.adapters

import android.content.Context
import android.graphics.drawable.Icon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.test.domain.entities.VoiceRecord
import com.test.voicenote.R
import com.test.voicenote.databinding.VoiceItemBinding
import com.test.voicenote.handlers.Player
import com.test.voicenote.utils.FormatterDateTime
import com.test.voicenote.utils.StatesPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate

class VoicesAdapter(
    private val scope: CoroutineScope,
    private val context: Context
) : ListAdapter<VoiceRecord, VoiceViewHolder>(VoiceDiffUtilCallback()) {

    private val formatter = FormatterDateTime()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {
        return VoiceViewHolder(
            VoiceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {

        val item = getItem(position)
        val localDate = item.date
        val player = Player(context, item.uri)
        val durationValue = player.getDuration(scope)

        with(holder.binding) {

            currentProgress.value = 0f
            currentProgress.valueTo = durationValue.toFloat()
            currentDuration.text = formatter.getDuration(0)

            title.text = item.name

            date.text =
                if (localDate == LocalDate.now())
                    date.resources.getString(R.string.today_text_for_voice_item, " ")
                else formatter.getDate(localDate)

            time.text = time.resources.getString(R.string.time_text_for_voice_item, item.time)

            duration.text =
                duration.resources.getString(
                    R.string.duration_text_for_voice_item,
                    formatter.getDuration(durationValue)
                )

            playPauseButton.setOnClickListener {
                playPauseOnClick(player)
            }

            currentProgress.addOnChangeListener { _, value, fromUser ->
                if (fromUser) player.seekTo(value.toInt())
            }

            player.isStarted.onEach { isStarted ->
                currentProgress.isEnabled = isStarted
            }.launchIn(scope)

            player.isPlaying.onEach { isPlaying ->
                if (isPlaying) setButtonIcon(playPauseButton, R.drawable.pause_button_foreground)
                else setButtonIcon(playPauseButton, R.drawable.play_button_foreground)
            }.launchIn(scope)

            player.position.onEach { pos ->
                currentDuration.text = formatter.getDuration(pos)
                currentProgress.value = pos.toFloat()
            }.launchIn(scope)
        }
    }

    private fun playPauseOnClick(player: Player) {
        when (player.state) {
            StatesPlayer.STOPPED -> player.start()
            StatesPlayer.PAUSED -> player.resume()
            StatesPlayer.STARTED -> player.pause()
        }
    }

    private fun setButtonIcon(button: AppCompatImageButton, id: Int) {
        button.setImageIcon(Icon.createWithResource(button.context, id))
    }
}

class VoiceViewHolder(val binding: VoiceItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class VoiceDiffUtilCallback : DiffUtil.ItemCallback<VoiceRecord>() {
    override fun areItemsTheSame(
        oldItem: VoiceRecord,
        newItem: VoiceRecord
    ): Boolean {
        return oldItem.uri == newItem.uri
    }

    override fun areContentsTheSame(
        oldItem: VoiceRecord,
        newItem: VoiceRecord
    ): Boolean {
        return oldItem.uri == newItem.uri
                && oldItem.name == newItem.name
                && oldItem.date == newItem.date
                && oldItem.time == newItem.time
    }
}