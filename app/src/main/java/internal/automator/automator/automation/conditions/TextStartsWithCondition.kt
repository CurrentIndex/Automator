package internal.automator.automator.automation.conditions

import cn.vove7.andro_accessibility_api.viewfinder.AcsNode
import cn.vove7.andro_accessibility_api.viewfinder.ConditionGroup
import cn.vove7.andro_accessibility_api.viewfinder.MatchCondition

fun ConditionGroup.textStartsWith(prefix: String) = link(TextStartsWithCondition(prefix))
class TextStartsWithCondition(private val prefix: String) : MatchCondition {
    override fun invoke(node: AcsNode) = node.text?.startsWith(prefix) ?: false
}