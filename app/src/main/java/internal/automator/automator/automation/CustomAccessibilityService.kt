package internal.automator.automator.automation

import cn.vove7.andro_accessibility_api.AccessibilityApi
import cn.vove7.andro_accessibility_api.AppScope

class CustomAccessibilityService : AccessibilityApi() {
    override val enableListenAppScope: Boolean = true

    override fun onPageUpdate(currentScope: AppScope) {
        super.onPageUpdate(currentScope)
//        println(currentScope.packageName + " " + currentScope.pageName)
    }
}