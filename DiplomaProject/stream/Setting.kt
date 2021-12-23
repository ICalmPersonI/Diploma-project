package stream

import entity.DisplayResolution
import java.awt.event.WindowEvent
import javax.swing.*
import javax.swing.border.EmptyBorder

class Setting(private val protocol: String): JFrame() {

    private val resolutions: Map<String, DisplayResolution> = mapOf(
        "1366x768" to DisplayResolution(1366, 768),
        "360x640" to DisplayResolution(360, 640)
    )
    private val videoCodecs: List<String> = listOf(
        "mp1v", "mp2v", "mp4v", "SVQ1", "SVQ3", "DVDv", "WMV1", "WMV2", "WMV3", "DVSD", "MJPG",
        "H263", "h264", "theo", "IV20", "IV40", "RV10", "cvid", "VP31", "FLV1", "CYUV", "HFYU", "MSVC", "MRLE",
        "AASC", "FLIC", "QPEG", "VP8"
    )
    private val audioCodecs: List<String> = listOf("mpga", "mp3", "mp4a", "a52", "vorb", "opus", "spx", "flac")

    private val videoCodec: JPanel =
        comboBox(
            arrayOf(videoCodecs[2], videoCodecs[10], videoCodecs[12]) , "Video Codec:"
        )

    private val audioCodec: JPanel =
        comboBox(
            when (protocol) {
                "http" -> arrayOf(audioCodecs[0], audioCodecs[1], audioCodecs[2])
                "rtp" -> audioCodecs.toTypedArray()
                 else -> arrayOf(audioCodecs[0], audioCodecs[2], audioCodecs[3], audioCodecs[4], audioCodecs[5])
            }
            , "Audio Codec:"
        )

    private val displayResolution: JPanel =
        comboBox(arrayOf("1366x768", "360x640"),
            "Display Resolution:")

    private val bitRate: JPanel = bitRatePane()
    private lateinit var videoCodecComboBox: JComboBox<String>
    private lateinit var audioCodecComboBox: JComboBox<String>
    private lateinit var displayResolutionComboBox: JComboBox<String>
    private lateinit var bitRateValue: JTextField
    private val startButton: JButton = JButton("Start")

    init {
        setSize(200, 230)

        val contentPane: JPanel = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)
        contentPane.border = EmptyBorder(0, 10, 0, 10)

        startButton.addActionListener {
            Stream(
                videoCodecComboBox.selectedItem as String,
                audioCodecComboBox.selectedItem as String,
                bitRateValue.text.toInt(),
                resolutions[displayResolutionComboBox.selectedItem]!!,
                protocol
            )
            this.dispatchEvent(WindowEvent(this, WindowEvent.WINDOW_CLOSING))
        }

        contentPane.add(videoCodec)
        contentPane.add(audioCodec)
        contentPane.add(displayResolution)
        contentPane.add(bitRate)
        contentPane.add(startButton)
        add(contentPane)

        setLocationRelativeTo(null)
        isResizable = false
        isVisible = true
    }

    private fun comboBox(options: Array<String>, name: String): JPanel {
        val contentPane: JPanel = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)

        contentPane.add(JLabel(name))
        when (name) {
            "Video Codec:" -> {
                videoCodecComboBox = JComboBox(options)
                contentPane.add(videoCodecComboBox)
            }
            "Audio Codec:" -> {
                audioCodecComboBox = JComboBox(options)
                contentPane.add(audioCodecComboBox)
            }
            "Display Resolution:" -> {
                displayResolutionComboBox = JComboBox(options)
                contentPane.add(displayResolutionComboBox)
            }
        }

        return contentPane
    }

    private fun bitRatePane(): JPanel {
        val contentPane: JPanel = JPanel()
        contentPane.layout = BoxLayout(contentPane, BoxLayout.Y_AXIS)

        bitRateValue = JTextField("4096", 1)
        contentPane.add(JLabel("BitRate:"))
        contentPane.add(bitRateValue)

        return contentPane
    }

}