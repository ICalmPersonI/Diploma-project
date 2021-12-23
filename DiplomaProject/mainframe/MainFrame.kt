package mainframe

import other.TaskController
import uk.co.caprica.vlcj.factory.MediaPlayerFactory
import uk.co.caprica.vlcj.player.base.MediaPlayer
import java.awt.BorderLayout
import java.awt.Canvas
import java.awt.Color
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.*


class MainFrame : JFrame() {

    private val factory: MediaPlayerFactory
    private val mediaPlayer: MediaPlayer

    init {
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        setSize(800, 600)
        title = "Diploma Project"
        layout = BorderLayout()

        val video: Canvas = Canvas()
        video.background = Color.BLACK

        factory = MediaPlayerFactory()

        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer()
        mediaPlayer.videoSurface().set(factory.videoSurfaces().newVideoSurface(video))

        mediaPlayer.controls().repeat = false

        val contentPane: JPanel = ContentPane(mediaPlayer)
        contentPane.add(video, BorderLayout.CENTER)
        add(contentPane)

        isVisible = true

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                mediaPlayer.release()
                TaskController.shutdown()
            }
        })
    }
}