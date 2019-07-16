import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.option.Option
import com.atlassian.jira.issue.customfields.manager.OptionsManager

  static void priorityMapping(MutableIssue issue) {
    def priority = issue.priority
    if(priority.name == "Highest") 
        issue.priority = getPriorityByName("P0-Blocker")
    else if(priority.name == "High") 
        issue.priority = getPriorityByName("P1-Critical")
    else if(priority.name == "Medium") 
        issue.priority = getPriorityByName("P2-Major")
    else if(priority.name == "Low") 
        issue.priority = getPriorityByName("P3-Minor")
    else if(priority.name == "Lowest") 
        issue.priority = getPriorityByName("P4-Trivial")
  }

  static Object resolutionMapping(MutableIssue issue) {
    Logger log = Logger.getLogger("")

    def resolutionManager = ComponentAccessor.getComponent(ResolutionManager)
    def resolution = (WorkflowUtils.getCFValue(issue, "解决方式") as Option).value
    if (resolution == "已解决")
      issue.resolution = resolutionManager.getResolutionByName("已解决")
    else if (resolution == "设计如此")
      issue.resolution = resolutionManager.getResolutionByName("设计如此")
    else if (resolution == "暂缓处理")
      issue.resolution = resolutionManager.getResolutionByName("暂缓处理")
    else if (resolution == "设计变更")
      issue.resolution = resolutionManager.getResolutionByName("设计变更")
    else if (resolution == "重复提交")
      issue.resolution = resolutionManager.getResolutionByName("重复提交")
    else if (resolution == "无法再次复现")
      issue.resolution = resolutionManager.getResolutionByName("无法再次复现")
    else if (resolution == "外部原因")
      issue.resolution = resolutionManager.getResolutionByName("外部原因")
    else if (resolution == "暂缓发布")
      issue.resolution = resolutionManager.getResolutionByName("暂缓发布")
    else 
      issue.resolution = resolutionManager.getResolutionByName("完成")


          def repository = getCFValue(issue, "Git项目")
    def branchUrl = getCFValue(issue, "Git分支")


import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption

def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueObject("BETA-450")
 
IssueUtils.setCFValue(issue, "端类型", ["H5"])

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

def issueManager = ComponentAccessor.getIssueManager()
def issue = issueManager.getIssueObject("TV-453")

//def reopenCountString = IssueUtils.getCFValue(issue, "激活计数") as String;
//def reopenCount = (reopenCountString != null) ? (int)Float.parseFloat(reopenCountString) : 0;
//reopenCount++;
//IssueUtils.setCFValue(issue, "激活计数", reopenCount.toString());

IssueUtils.setCFValue(issue, "激活计数", 0);
IssueUtils.setCFValue(issue, "已发布端", ["盒子端"], true);

//def customFieldManager = ComponentAccessor.getCustomFieldManager()
//def cf = customFieldManager.getCustomFieldObjects(issue).find{it.name == "激活计数"}
//def customFieldValue = cf.getValue(issue);
//customFieldValue instanceof Double
//def fieldConfig = cf.getRelevantConfig(issue)
//cf.getCustomFieldType().getDefaultValue(fieldConfig)
//cf.getValue(issue)
//def reopenCountString = IssueUtils.getCFValue(issue, "激活计数") as String;
//def reopenCount = (reopenCountString != null) ? (int)Float.parseFloat(reopenCountString) : 0;
//reopenCount++;
//reopenCount.toString()
//def _user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
//ComponentAccessor.getIssueManager().updateIssue(_user, issue, EventDispatchOption.ISSUE_UPDATED, false)
//def changeHolder = new DefaultIssueChangeHolder()
//cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), Double.parseDouble(reopenCount.toString())), changeHolder)

//IssueUtils.setCFValue(issue, "激活计数", 2);

//IssueUtils.setCFValue(issue, "已发布端", ["盒子端"], true)
//def customFieldManager = ComponentAccessor.getCustomFieldManager()
//def cf = customFieldManager.getCustomFieldObjects(issue).find{it.name == "已发布端"}
//def fieldConfig = cf.getRelevantConfig(issue)
//fieldConfig.getCustomField().getCustomFieldType() instanceof MultiSelectCFType
//def fieldValue = issue.getCustomFieldValue(cf) as ArrayList
//if(fieldValue == null) {
//  fieldValue = new ArrayList();
//}
/*fieldValue.clear()
def option = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == "H5"}
if(option != null) {
  fieldValue.add(option)
}
fieldValue.class
issue.setCustomFieldValue(cf, fieldValue)
def _user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
ComponentAccessor.getIssueManager().updateIssue(_user, issue, EventDispatchOption.ISSUE_UPDATED, false)
*/
/*if(fieldConfig.getCustomField().getCustomFieldType() instanceof GenericTextCFType) {
  setCFNormalValue(issue, cf, value as String)
} else if(fieldConfig.getCustomField().getCustomFieldType() instanceof MultiSelectCFType) {
  SetCFMultiSelectValue(issue, cf, fieldConfig, value as List<String>)
} else if(fieldConfig.getCustomField().getCustomFieldType() instanceof SelectCFType) {
  SetCFSelectValue(issue, cf, fieldConfig, value as String)
} else {
  setCFNormalValue(issue, cf, value as String)
}
*/
//def allTerminal = IssueUtils.getCFValue(issue, "端类型") as ArrayList
//def publishedTerminal = IssueUtils.getCFValue(issue, "已发布端") as ArrayList
//allTerminal != null && publishedTerminal != null &&
//allTerminal.sort{it.value}.toString() == publishedTerminal.sort{it.value}.toString()