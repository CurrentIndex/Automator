package internal.automator.automator.automation.conditions

import cn.vove7.andro_accessibility_api.viewfinder.AcsNode
import cn.vove7.andro_accessibility_api.viewfinder.ConditionGroup
import cn.vove7.andro_accessibility_api.viewfinder.MatchCondition

fun ConditionGroup.textEndsWith(prefix: String) = link(TextEndsWithCondition(prefix))
class TextEndsWithCondition(private val prefix: String) : MatchCondition {
    override fun invoke(node: AcsNode) = node.text?.endsWith(prefix) ?: false
}