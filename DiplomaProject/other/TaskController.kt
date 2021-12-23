package other

import java.util.*

class TaskController {

    companion object {
        private val taskList: MutableList<Timer> = mutableListOf()

        fun add(task: TimerTask, delay: Long) {
            val timer: Timer = Timer()
            timer.scheduleAtFixedRate(task, delay, delay)
            taskList.add(timer)
        }

        fun shutdown() { taskList.forEach { it.cancel() } }
    }

}