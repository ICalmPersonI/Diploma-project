package benchmark

import oshi.SystemInfo
import oshi.hardware.CentralProcessor


class HardwareInfo {
    private val si = SystemInfo()
    private val hal = si.hardware
    private val cpu = hal.processor
    private val memory = hal.memory

    private var prevTicks = LongArray(CentralProcessor.TickType.values().size)

    val totalRAM: Int = (memory.total / 1048576).toInt()

    fun cpuLoad(): Float {
        val cpuLoad = cpu.getSystemCpuLoadBetweenTicks(prevTicks) * 100
        prevTicks = cpu.systemCpuLoadTicks
        return cpuLoad.toFloat()
    }

    fun memoryUsage(): Float = ((memory.total - memory.available) / 1048576).toFloat()
}