package internal.automator.automator.automation.common

import cn.vove7.andro_accessibility_api.api.back
import kotlinx.coroutines.delay

object Automation {
    suspend fun backCount(count: Int): Boolean {
        for (i in 0 until count) {
            if (!back()) return false
            delay(2000)
        }
        return true
    }
}
