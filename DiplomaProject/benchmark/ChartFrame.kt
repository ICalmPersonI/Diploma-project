package benchmark

import java.awt.GridLayout
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class ChartFrame(titleName: String, charts: List<DynamicChart>): JFrame() {

    init {
        setSize(600, 500)
        defaultCloseOperation = 0
        title = titleName

        val contentPane: JPanel = JPanel()
        contentPane.border = EmptyBorder(5, 10, 5, 10)
        val gridLayout: GridLayout = GridLayout(2, 1)
        contentPane.layout = gridLayout

        charts.forEach { contentPane.add(it.chart) }

        add(contentPane)

        isVisible = true
    }
}