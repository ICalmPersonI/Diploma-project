package stream

import other.TaskController
import benchmark.ChartFrame
import benchmark.DynamicChart
import benchmark.HardwareInfo
import entity.DisplayResolution
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.media.*
import uk.co.caprica.vlcj.player.base.MediaPlayer
import java.awt.Color
import java.util.*
import kotlin.concurrent.timerTask


class Stream(
    private val videoCodec: String,
    private val audioCodec: String,
    private val bitRate: Int,
    private val dr: DisplayResolution,
    private val protocol: String
) {

    private val mediaPlayerFactory: MediaPlayerFactory
    private val mediaPlayer: MediaPlayer

    private var hwInfo: HardwareInfo = HardwareInfo()
    private val mediaStatistics = MediaStatistics()

    init {

        val media: String = "test.mp4" // "screen://" "dshow://"
        val videoOptions: String = formatVideoOptions();
        val streamOptions: String = formatStreamOptions(videoOptions, "localhost", 8080, "demo")

        mediaPlayerFactory = MediaPlayerFactory(media)
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer()

        when (protocol) {
            "http" -> {
                mediaPlayer.media().play(
                    media,
                    streamOptions,
                )
            }
            "rtp" -> {
                mediaPlayer.media().play(
                    media,
                    streamOptions,
                    ":no-sout-rtp-sap",
                    ":no-sout-standard-sap",
                    ":sout-all",
                    ":sout-keep"
                );
            }
            "rtsp" -> {
                mediaPlayer.media().play(
                    media,
                    streamOptions,
                    "encoding etc",
                    ":no-sout-rtp-sap",
                    ":no-sout-standard-sap",
                    ":sout-all",
                    ":sout-keep",
                )
            }
        }

        val charts: List<DynamicChart> = listOf(
            DynamicChart("BitRate", 0, bitRate + 4000, Color.RED),
            DynamicChart("CpuLoad", 0, 100, Color.GREEN),
            DynamicChart("MemoryUsage", 0, hwInfo.totalRAM, Color.BLUE),
            DynamicChart("AudioBuffersLost", 0, 0, Color.MAGENTA)
        )
        ChartFrame("Output Statistic", charts)

        charts.forEach { TaskController.add(createTask(it), 1000L) }
    }

    private fun formatVideoOptions(): String {
        var options: String = ""
        options += ":sout=#transcode{"
        options += "vcodec=$videoCodec,"
        options += "vb=$bitRate,"
        options += "fps=25,"
        options += "scale=1,"
        options += "acodec=$audioCodec,"
        options += "ab=256," //128
        options += "channels=2,"
        options += "samplerate=44100,"
        options += "weight=${dr.weight},"
        options += "height=${dr.height}"
        options += "}"

        return options
    }

    private fun formatStreamOptions(deviseOptions: String, address: String, port: Int, id: String): String {
        return when (protocol) {
            "http" -> "$deviseOptions:duplicate{dst=std{access=http,mux=ts,dst=$address:$port}}"
            "rtp" -> "$deviseOptions:rtp{dst=$address,port=$port,mux=ts}"
            else -> "$deviseOptions:rtp{sdp=rtsp://@$address:$port/$id}"
        }
    }

    private fun createTask(dc: DynamicChart): TimerTask {
        return timerTask {

            mediaPlayer.media().info().statistics(mediaStatistics)

            when (dc.chartName) {
                "BitRate" -> dc.update(mediaStatistics.demuxBitrate() * 1000)
                "CpuLoad" -> dc.update(hwInfo.cpuLoad())
                "MemoryUsage" -> dc.update(hwInfo.memoryUsage())
                "AudioBuffersLost" -> dc.update(mediaStatistics.audioBuffersLost().toFloat())
            }

        }
    }
}