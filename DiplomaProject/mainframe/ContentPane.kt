package mainframe

import benchmark.ChartFrame
import benchmark.DynamicChart
import benchmark.HardwareInfo
import loadsimulation.Simulation
import other.TaskController
import stream.Setting
import uk.co.caprica.vlcj.player.base.MediaPlayer
import java.awt.BorderLayout
import java.awt.Color
import java.util.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.timerTask


class ContentPane(private val mediaPlayer: MediaPlayer) : JPanel() {

    private val playButton: JButton = JButton("Play")
    private val pauseButton: JToggleButton = JToggleButton("Pause")
    private val streamButton: JButton = JButton("Start Stream")
    private val loadSimulationButton: JButton = JButton("Load Simulation")
    private val stopLoadSimulation: JButton = JButton("Emergency load stop")
    private val protocol: JComboBox<String> = JComboBox(arrayOf("http", "rtp", "rtsp"))
    private val volume: JSlider = JSlider()

    private val httpUrl: String = "http://localhost:8080"
    private val rtsUrl: String = "rtp://localhost:8080"
    private val rtspUrl: String = "rtsp://@localhost:8080/demo"

    private val hwInfo: HardwareInfo = HardwareInfo()

    private var loadSimulation: Simulation? = null

    init {
        background = Color.BLACK
        layout = BorderLayout()

        val contentPane: JPanel = JPanel()
        contentPane.border = EmptyBorder(4, 4, 4, 4)
        contentPane.layout = BoxLayout(contentPane, BoxLayout.X_AXIS)

        volume.minimum = 50
        volume.maximum = 200
        volume.value = 100

        contentPane.add(playButton)
        contentPane.add(pauseButton)
        contentPane.add(streamButton)
        contentPane.add(loadSimulationButton)
        contentPane.add(stopLoadSimulation)
        contentPane.add(protocol)
        contentPane.add(volume)

        add(contentPane, BorderLayout.NORTH)

        listeners()
    }

    private fun listeners() {
        playButton.addActionListener { playListenerBody() }
        pauseButton.addActionListener { mediaPlayer.controls().pause() }
        streamButton.addActionListener { Setting(protocol.selectedItem as String) }
        loadSimulationButton.addActionListener {
            loadSimulation = null
            loadSimulation = Simulation(
                when (protocol.selectedItem) {
                    "http" -> httpUrl
                    "rtp" -> rtsUrl
                    else -> rtspUrl
                }
            )
            loadSimulation!!.start()
        }
        stopLoadSimulation.addActionListener { loadSimulation!!.interruptAll() }
        volume.addChangeListener { changeVolume(volume.value) }
    }

    private fun playListenerBody() {
        mediaPlayer.media().startPaused(
            when (protocol.selectedItem) {
                "http" -> httpUrl
                "rtp" -> rtsUrl
                else -> rtspUrl
            }
        )
        mediaPlayer.controls().play()

        val info = mediaPlayer.media().info().statistics()

        val charts: List<DynamicChart> = listOf(
            DynamicChart("BitRate", 0, (info.demuxBitrate() * 10000).toInt() + 12000, Color.RED),
            DynamicChart("CpuLoad", 0, 100, Color.GREEN),
            DynamicChart("MemoryUsage", 0, hwInfo.totalRAM, Color.BLUE),
            DynamicChart("Drops", 0, 0, Color.MAGENTA)
        )

        ChartFrame("Input Statistic", charts)

        charts.forEach { TaskController.add(createTask(it), 1000L) }
    }

    private fun createTask(dc: DynamicChart): TimerTask {
        return timerTask {

            val info = mediaPlayer.media().info().statistics()
            when (dc.chartName) {
                "BitRate" -> dc.update(info.demuxBitrate() * 10000)
                "CpuLoad" -> dc.update(hwInfo.cpuLoad())
                "MemoryUsage" -> dc.update(hwInfo.memoryUsage())
            }
        }
    }

    private fun changeVolume(value: Int) = mediaPlayer.audio().setVolume(value)
}