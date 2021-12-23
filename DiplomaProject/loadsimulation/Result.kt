package loadsimulation

import entity.Errors
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.Color
import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class Result(private val errorsList: MutableList<Errors>, private val numberOfChart: Int) : JFrame() {

    init {
        setSize(600, 500)
        defaultCloseOperation = 0
        title = "Result"

        val contentPane: JPanel = JPanel()
        contentPane.border = EmptyBorder(5, 10, 5, 10)
        val gridLayout: GridLayout = GridLayout(2, 1)
        contentPane.layout = gridLayout


        val maxAverageErrorsValue: List<String> = getTotalMaxAverageErrorsValues()

        contentPane.add(createChartPanel("Demux Discontinuity", 1, maxAverageErrorsValue[0]))
        contentPane.add(createChartPanel("Demux Corrupted", 2, maxAverageErrorsValue[1]))
        contentPane.add(createChartPanel("Audio BuffersLost", 3, maxAverageErrorsValue[2]))
        contentPane.add(createChartPanel("Pictures Lost", 4, maxAverageErrorsValue[3]))

        add(contentPane)

        isVisible = true
    }

    private fun getTotalMaxAverageErrorsValues(): List<String> {
        val demuxDiscontinuityMax: MutableList<Int> = mutableListOf()
        val demuxCorruptedMax: MutableList<Int> = mutableListOf()
        val audioBuffersLostMax: MutableList<Int> = mutableListOf()
        val picturesLostMax: MutableList<Int> = mutableListOf()

        for (i in 1..numberOfChart) {
            demuxDiscontinuityMax.add(errorsList.filter { it.playerId == i }.maxOf { it.demuxDiscontinuity })
            demuxCorruptedMax.add(errorsList.filter { it.playerId == i }.maxOf { it.demuxCorrupted })
            audioBuffersLostMax.add(errorsList.filter { it.playerId == i }.maxOf { it.audioBuffersLost })
            picturesLostMax.add(errorsList.filter { it.playerId == i }.maxOf { it.picturesLost })
        }

        val demuxDiscontinuityAverage: Int  = demuxDiscontinuityMax.average().toInt()
        val demuxCorruptedAverage: Int = demuxCorruptedMax.average().toInt()
        val audioBuffersLostAverage: Int = audioBuffersLostMax.average().toInt()
        val picturesLostAverage: Int = picturesLostMax.average().toInt()

        val textTemplate: (String, Int) -> (String)
                = { error, value -> "The average maximum error '$error' of all clients: $value" }

        return listOf(
            textTemplate.invoke("Demux Discontinuity", demuxDiscontinuityAverage),
            textTemplate.invoke("Demux Corrupted", demuxCorruptedAverage),
            textTemplate.invoke("Audio BuffersLost", audioBuffersLostAverage),
            textTemplate.invoke("Pictures Lost", picturesLostAverage)
        )
    }

    private fun createChartPanel(name: String, parameter: Int, average: String): JPanel {
        val dataSet: XYDataset = createDataset(parameter)
        val chart: JFreeChart =
            ChartFactory.createXYLineChart(name + "\n$average", "Time", "Errors", dataSet)

        val plot = chart.xyPlot
        val axis = plot.domainAxis
        axis.setRange(0.0, 600.0)

        customizeChart(chart)

        return ChartPanel(chart)
    }

    private fun createDataset(parameter: Int): XYDataset {
        val dataSet: XYSeriesCollection = XYSeriesCollection()
        val listOfData: Array<XYSeries?> = arrayOfNulls(numberOfChart)

        for (i in 1..numberOfChart) listOfData[i - 1] = XYSeries("Client #$i")

        listOfData.forEach { it!!.add(0, 0) }

        listOfData.forEachIndexed { i, elem ->
            errorsList.filter { it.playerId == i + 1 && when (parameter) {
                1 -> it.demuxDiscontinuity
                2 -> it.demuxCorrupted
                3 -> it.audioBuffersLost
                else -> it.picturesLost
            } != 0 }
                .forEach { elem!!.add(it.time, when (parameter) {
                    1 -> it.demuxDiscontinuity
                    2 -> it.demuxCorrupted
                    3 -> it.audioBuffersLost
                    else -> it.picturesLost
                }) }
        }

        listOfData.forEach { dataSet.addSeries(it) }

        return dataSet
    }

    private fun customizeChart(chart: JFreeChart) {
        val plot = chart.xyPlot
        val renderer = XYLineAndShapeRenderer()
        val color: List<Color> = listOf(
            Color(153, 0 , 0),
            Color(0, 0 , 153),
            Color(0, 102, 0),
            Color(51, 0, 0),
            Color(51, 51, 51),
            Color(102, 0, 153),
            Color(0, 102, 102),
            Color(47,79,79),
            Color(184,134,11),
            Color(0,0,0),
        )
        for (i in 1..numberOfChart) renderer.setSeriesPaint(i - 1, color[i - 1])

        plot.backgroundPaint = Color.WHITE
        plot.domainGridlinePaint = Color.BLACK
        plot.rangeGridlinePaint = Color.BLACK
    }

}