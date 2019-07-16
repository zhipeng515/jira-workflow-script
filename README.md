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

static boolean checkGitlabLink(String gitHostUrl, String gitApiVersion, String privateToken, String repositoryUrl, String branch)

```groovy
IssueUtils.checkGitlabLink("http://192.168.120.96", "api/v4", "GS799u8jXNKwyJ2gSBib", "/developer-server/GameServer.git", "feature/pay");
```

* static boolean createGitlabLink(Issue issue, String repository, String branch, ApplicationUser user = null)

```groovy
IssueUtils.createGitlabLink(issue, "http://192.168.120.96/developer-server/GameServer.git", "feature/pay");
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

* static boolean isUserMemberOfRole(List<String> roles, Project project, ApplicationUser user = null)
* static boolean isUserMemberOfRole(List<String> roles, Project project, String userKeyName)

```groovy
// roles: project roles
UserUtils.isUserMemberOfRole(["Developer","Designer"], issue.getProjectObject())
UserUtils.isUserMemberOfRole(["Developer","Designer"], issue.getProjectObject(), "john")
```

* static boolean addUserToGroup(String userKeyName, String groupName)

```groovy
// groupName: crowd group name
UserUtils.addUserToGroup("john", "vpnusers");
```

* static boolean removeUserFromGroup(String userKeyName, String groupName)

```groovy
// groupName: crowd group name
UserUtils.removeUserFromGroup("john", "vpnusers");
```

## Workflow

* static void executeTransition(Issue issue, String actionNamePattern, ApplicationUser user = null)

```groovy
// actionNamePattern: workflow transition name
WorkflowUtils.executeTransition(issue, "Start");
```
