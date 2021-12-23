package benchmark

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.JFreeChart
import org.jfree.data.time.Millisecond
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import org.jfree.data.xy.XYDataset
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import javax.swing.JPanel


class DynamicChart(val chartName: String, private val min: Int, private val max: Int, private val color: Color) {

    val chart: JPanel = JPanel(BorderLayout())
    private val series: TimeSeries = TimeSeries(chartName)

    init {
        val dataSet: TimeSeriesCollection = TimeSeriesCollection(series)
        val jFreeChart: JFreeChart = createChart(dataSet)

        val chartPanel: ChartPanel = ChartPanel(jFreeChart)

        jFreeChart.xyPlot.renderer.setSeriesPaint(0, color)

        chart.add(chartPanel)
        chartPanel.preferredSize = Dimension(500, 270)
    }

    private fun createChart(dataset: XYDataset): JFreeChart {
        val result = ChartFactory.createTimeSeriesChart(
            chartName,
            "Time",
            "Value",
            dataset,
            true,
            true,
            false
        )

        val plot = result.xyPlot
        var axis = plot.domainAxis
        axis.isAutoRange = true
        axis.fixedAutoRange = 10000.0 // 60 seconds
        axis = plot.rangeAxis
        if (max != 0) axis.setRange(min.toDouble(), max.toDouble())
        return result
    }

    fun update (value: Float) { series.addOrUpdate(Millisecond(), value) }
}