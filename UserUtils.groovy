import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.context.GlobalIssueContext

import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor

import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;

import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService.RemoteIssueLinkListResult

import com.atlassian.crowd.embedded.api.CrowdService
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.crowd.embedded.api.User

import java.util.logging.Logger

class UserUtils {

  static ApplicationUser getUserByKey(String userKeyName) {
      return ComponentAccessor.getUserUtil().getUserByKey(userKeyName);
  }

  static boolean isUserMemberOfRole(List<String> roles, Project project, ApplicationUser user = null) {
    ApplicationUser _user = user == null ? ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() : user;

    ProjectManager projectManager = ComponentManager.getComponentInstanceOfType(ProjectManager.class)
    ProjectRoleManager projectRoleManager = ComponentManager.getComponentInstanceOfType(ProjectRoleManager.class)

    return roles.any { String role ->
      ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(projectRoleManager.getProjectRole(role), project)
      projectRoleActors.getUsers().contains(_user)
    }
  }
  static boolean isUserMemberOfRole(List<String> roles, Project project, String userKeyName) {
    return isUserMemberOfRole(roles, project, getUserByKey(userKeyName));
  }

    static User applicaionUserToUser(ApplicationUser user) {
    CrowdService crowdService = ComponentAccessor.crowdService
    return crowdService.getUser(user.getUsername())
  }

  static boolean addUserToGroup(ApplicationUser user, String groupName) {
    CrowdService crowdService = ComponentAccessor.crowdService
    Group group = crowdService.getGroup(groupName) 
    User _user = applicaionUserToUser(user)
    if (!crowdService.isUserDirectGroupMember(_user, group)) {
      crowdService.addUserToGroup(_user, group)
      return true
    }
    return false
  }
  static boolean addUserToGroup(String userKeyName, String groupName) {
      addUserToGroup(getUserByKey(userKeyName), groupName)
  }

  static boolean removeUserFromGroup(ApplicationUser user, String groupName) {
    CrowdService crowdService = ComponentAccessor.crowdService
    Group group = crowdService.getGroup(groupName)
    User _user = applicaionUserToUser(user)
    if (!crowdService.isUserDirectGroupMember(_user, group)) {
      crowdService.removeUserFromGroup(_user, group)
      return true
    }
    return false
  }
  static boolean removeUserFromGroup(String userKeyName, String groupName) {
      removeUserFromGroup(getUserByKey(userKeyName), groupName)
  }
}
