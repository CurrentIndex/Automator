package internal.automator.automator.automation.common

object GoodsFormatter {
    fun formatPrice(it: String): Int {
        val original = it
            .replace(" ", "")
            .replace("¥", "")
            .replace("￥", "")
            .replace("起", "")
        return if (original.contains(".")) original.toFloat().toInt() else original.toInt()
    }

    fun formatSalesVolume(it: String): Int {
        var s = it
            .replace(" ", "")
            .replace("已售", "")
        var m = 1
        if (s.endsWith("万")) {
            s = s.replace("万", "")
            m = 10000
        }
        // 2.8 * 10000 = 28000.0 = 28000
        if (s.contains(".")) return (s.toFloat() * m).toInt()
        return s.toInt() * m
    }
}