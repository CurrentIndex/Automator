package internal.automator.automator.automation.common

object Time {
    fun isSameDate(a: Long, b: Long): Boolean {
        return a / (24 * 60 * 60 * 1000L) == b / (24 * 60 * 60 * 1000L)
    }
}