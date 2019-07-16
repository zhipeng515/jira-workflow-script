# Jira Workflow Scripts

## Issue

* static Object getCFValue(Issue issue, String cfName)

```groovy
// gitRepositoryUrl: custom field name
IssueUtils.getCFValue(issue, "gitRepositoryUrl")
```

* static void setCFValue(MutableIssue issue, String cfName, Object value, boolean clear = false)

```groovy
IssueUtils.setCFValue(issue, "gitRepositoryUrl", "https://github.com/zhipeng515/jira-workflow-script.git")
// clear: add or overwrite
IssueUtils.setCFValue(issue, "branches", ["master","feature/giftbox"]) // branches: ["master","feature/giftbox"]
IssueUtils.setCFValue(issue, "branches", ["develop"]) // branches: ["master","feature/giftbox","develop"]
IssueUtils.setCFValue(issue, "branches", ["feature/pay"], true) // branches: ["feature/pay"]
```

* static void updateResolution(Issue issue, Resolution resolution = null, ApplicationUser user = null)

```groovy
import com.atlassian.jira.config.ResolutionManager
import com.atlassian.jira.component.ComponentAccessor

def resolutionManager = ComponentAccessor.getComponent(ResolutionManager)
def resolution = resolutionManager.getResolutionByName("Done")
IssueUtils.updateResolution(issue, resolution);
```

* static List<RemoteIssueLink> getRemoteIssueLinks(Issue issue, ApplicationUser user = null)

```groovy
IssueUtils.getRemoteIssueLinks(issue);
```

* static boolean createConfluenceLink(Issue issue, String prdUrl, ApplicationUser user = null)

```groovy
IssueUtils.createConfluenceLink(issue, "http://confluence.company.com/pages/viewpage.action?pageId=1510304");
```

* static boolean createGitlabLink(Issue issue, String repository, String branch, ApplicationUser user = null)

```groovy
IssueUtils.createGitlabLink(issue, "https://github.com/zhipeng515/jira-workflow-script.git", "feature/pay");
```

* static boolean inStatus(List<String> statuses, Issue issue)

```groovy
// statuses: workflow status
IssueUtils.inStatus(["Open", "Reopen"], issue);
```

* static boolean hasSubtaskInType(Issue issue, String type)

```groovy
// type: issue type name
IssueUtils.hasSubtaskInType(issue, "DesignTask");
```

* static boolean allSubtasksInTypeDone(Issue issue, String type)

```groovy
// type: issue type name
IssueUtils.allSubtasksInTypeDone(issue, "DesignTask");
```

* static boolean allSubtasksInTypeIsStatus(Issue issue, String type, List<String> statuses)

```groovy
// statuses: workflow status
IssueUtils.allSubtasksInTypeIsStatus(issue, "DesignTask", ["Open","Reopen"]);
```

## User

## Workflow
