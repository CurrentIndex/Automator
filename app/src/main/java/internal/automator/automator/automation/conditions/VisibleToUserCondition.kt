package internal.automator.automator.automation.conditions;

import cn.vove7.andro_accessibility_api.viewfinder.AcsNode
import cn.vove7.andro_accessibility_api.viewfinder.ConditionGroup
import cn.vove7.andro_accessibility_api.viewfinder.MatchCondition


fun ConditionGroup.isVisibleToUser(visible: Boolean) =
    link(VisibleToUser(visible))

class VisibleToUser(private val visible: Boolean) : MatchCondition {
    override fun invoke(node: AcsNode) = (node.isVisibleToUser == visible)
}