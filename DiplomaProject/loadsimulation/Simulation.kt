package loadsimulation

import other.TaskController
import entity.Errors
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter
import java.util.*
import kotlin.concurrent.timerTask


class Simulation(private val url: String) {

    private val mediaPlayers: MutableMap<Int, MediaPlayer> = mutableMapOf()
    private val errorsList: MutableList<Errors> = mutableListOf()

    private val numberOfClients: Int = 10

    private var time: Long = 0

    fun start() {
        for (i in 1..numberOfClients) {
            val factory: MediaPlayerFactory = MediaPlayerFactory(url)
            val mediaPlayer: MediaPlayer = factory.mediaPlayers().newMediaPlayer()
            mediaPlayer.events().addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
                override fun finished(mp: MediaPlayer) {
                }
            })
            mediaPlayers[i] = mediaPlayer
        }

        mediaPlayers.forEach {
            it.value.audio().setVolume(1)
            it.value.media().play(url)
            TaskController.add(createTask(it.value, it.key), 10000L)
        }

        val timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            val runningPlayers: Int = mediaPlayers.filter { !it.value.status().isPlaying }.count()
            if (runningPlayers == mediaPlayers.size) {
                interruptAll()
                timer.cancel()
            }
        }, 1000L, 1000L)
    }

    fun interruptAll() {
        mediaPlayers.forEach { it.value.release() }
        Result(errorsList, numberOfClients)
    }

    private fun createTask(mediaPlayer: MediaPlayer, playerId: Int): TimerTask {
        return timerTask {
            val info = mediaPlayer.media().info().statistics()
            errorsList.add(
                Errors(
                    playerId,
                    info.demuxDiscontinuity(),
                    info.demuxCorrupted(),
                    info.audioBuffersLost(),
                    info.picturesLost(),
                    time
                )
            )
            time++
        }
    }
}
