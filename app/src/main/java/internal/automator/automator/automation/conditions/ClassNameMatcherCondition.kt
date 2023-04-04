package internal.automator.automator.automation.conditions;

import cn.vove7.andro_accessibility_api.viewfinder.AcsNode
import cn.vove7.andro_accessibility_api.viewfinder.ConditionGroup
import cn.vove7.andro_accessibility_api.viewfinder.MatchCondition


fun ConditionGroup.className(vararg classname: String) =
    link(ClassNameMatcherCondition(classname))

class ClassNameMatcherCondition(private val classname: Array<out String>) : MatchCondition {
    override fun invoke(node: AcsNode) = node.className?.let {
        classname.any { name -> it == name }
    } ?: false
}