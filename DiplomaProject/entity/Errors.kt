package entity

data class Errors(
    val playerId: Int,
    val demuxDiscontinuity: Int,
    val demuxCorrupted: Int,
    val audioBuffersLost: Int,
    val picturesLost: Int,
    val time: Long
)
