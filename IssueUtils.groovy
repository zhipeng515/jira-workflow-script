import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.priority.Priority;
import com.atlassian.jira.issue.resolution.Resolution;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.issue.link.RemoteIssueLink;
import com.atlassian.jira.bc.issue.IssueService.UpdateValidationResult
import com.atlassian.jira.bc.issue.IssueService.IssueResult
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.context.GlobalIssueContext
import com.atlassian.jira.issue.context.JiraContextNode

import com.atlassian.jira.issue.customfields.impl.MultiSelectCFType
import com.atlassian.jira.issue.customfields.impl.GenericTextCFType
import com.atlassian.jira.issue.customfields.impl.SelectCFType
import com.atlassian.jira.issue.customfields.impl.NumberCFType
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.config.FieldConfig

import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor

import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService.RemoteIssueLinkListResult

import com.atlassian.jira.config.ResolutionManager
import com.atlassian.jira.config.PriorityManager
import com.atlassian.jira.bc.issue.link.RemoteIssueLinkService
import com.atlassian.jira.issue.link.RemoteIssueLinkBuilder


import com.atlassian.applinks.api.ApplicationLinkService
//import com.atlassian.applinks.api.application.jira.JiraApplicationType
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType

import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

import java.util.logging.Logger

class IssueUtils {

    static Priority getPriorityByName(String name) {
        def priorityManager = ComponentAccessor.getComponent(PriorityManager)
        def priorities = priorityManager.getPriorities();
        Priority ret = null;
        priorities.each { priority -> 
            if(priority.name == name) {
            ret = priority;
            return;
            }
        }
        return ret;
    }

    static void updateIssue(MutableIssue issue, ApplicationUser user = null) {
        def _user = user == null ? ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() : user
        ComponentAccessor.getIssueManager().updateIssue(_user, issue, EventDispatchOption.ISSUE_UPDATED, false)
    }

    static Object getCFValue(Issue issue, String cfName) {
        def customFieldManager = ComponentAccessor.getCustomFieldManager()
        def cf = customFieldManager.getCustomFieldObjects(issue).find{it.name == cfName}
        def value = issue.getCustomFieldValue(cf)
        return value
    }

    static private void SetCFMultiSelectValue(MutableIssue issue, CustomField cf, List<String> value, boolean clear = false) {
        def fieldValue = issue.getCustomFieldValue(cf) as ArrayList
        if(fieldValue == null) {
            fieldValue = new ArrayList();
        }
        if(clear) {
            fieldValue.clear()
        }
        def fieldConfig = cf.getRelevantConfig(issue)
        value.each { optionValue ->
            def option = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == optionValue}
            if(option != null) {
                fieldValue.add(option)
            }
        }
        issue.setCustomFieldValue(cf, fieldValue)
        updateIssue(issue)
    }

    static private void SetCFSelectValue(MutableIssue issue, CustomField cf, String value) {
        def fieldValue = issue.getCustomFieldValue(cf) as ArrayList
        if(fieldValue == null) {
            fieldValue = new ArrayList();
        }
        def fieldConfig = cf.getRelevantConfig(issue)
        def option = ComponentAccessor.optionsManager.getOptions(fieldConfig)?.find {it.toString() == value}
        if(option != null) {
            fieldValue.add(option)
        }
        issue.setCustomFieldValue(cf, fieldValue)
        updateIssue(issue)
    }

    static private void setCFNormalValue(MutableIssue issue, CustomField cf, String value) {
        def changeHolder = new DefaultIssueChangeHolder()
        cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), value), changeHolder)
    }

    static private void setCFDoubleValue(MutableIssue issue, CustomField cf, Double value) {
        def changeHolder = new DefaultIssueChangeHolder()
        cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), value), changeHolder)
    }

    static private void setCFFloatValue(MutableIssue issue, CustomField cf, Float value) {
        def changeHolder = new DefaultIssueChangeHolder()
        cf.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(cf), value), changeHolder)
    }

    static void setCFValue(MutableIssue issue, String cfName, Object value, boolean clear = false) {
        def customFieldManager = ComponentAccessor.getCustomFieldManager()
        def cf = customFieldManager.getCustomFieldObjects(issue).find{it.name == cfName}
        if(cf.getCustomFieldType() instanceof GenericTextCFType) {
            setCFNormalValue(issue, cf, value as String)
        } else if(cf.getCustomFieldType() instanceof MultiSelectCFType) {
            SetCFMultiSelectValue(issue, cf, value as List<String>, clear)
        } else if(cf.getCustomFieldType() instanceof SelectCFType) {
            SetCFSelectValue(issue, cf, value as String)
        } else if(cf.getCustomFieldType() instanceof NumberCFType) {
            def fieldConfig = cf.getRelevantConfig(issue)
            def defaultValue = cf.getCustomFieldType().getDefaultValue(fieldConfig)
            if(defaultValue instanceof Double) {
                setCFDoubleValue(issue, cf, value as Double)
            } else if(defaultValue instanceof Double) {
                setCFFloatValue(issue, cf, value as Float)
            }
        } else {
            setCFNormalValue(issue, cf, value as String)
        }
    }

    static void updateResolution(Issue issue, Resolution resolution = null, ApplicationUser user = null) {
        Logger log = Logger.getLogger("")

        def _resolution = resolution == null ? issue.resolution : resolution
        def _user = user == null ? ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() : user

        IssueService issueService = ComponentAccessor.issueService
        IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
        issueInputParameters.setResolutionId(_resolution.id)
        issueInputParameters.setSkipScreenCheck(true)

        UpdateValidationResult updateValidationResult = issueService.validateUpdate(user, issue.id, issueInputParameters);
        if (updateValidationResult.isValid())
        {
            IssueResult updateResult = issueService.update(user, updateValidationResult);
            if (updateResult.isValid()) {
            log.info "Successfully set resolution on ${issue.key}."
            } else {
            log.info "Could not set resolution on issue: ${updateResult.errorCollection}"
            }
        } else {
            log.info("Could not set resolution on issue: ${updateValidationResult.errorCollection}")
        }
    }

    static List<RemoteIssueLink> getRemoteIssueLinks(Issue issue, ApplicationUser user = null) {
        def _user = user == null ? ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() : user
        RemoteIssueLinkListResult result = ComponentAccessor.getComponentOfType(RemoteIssueLinkService.class).getRemoteIssueLinksForIssue(_user, issue);
        return result.getRemoteIssueLinks();
    }

    static boolean createConfluenceLink(Issue issue, String prdUrl, ApplicationUser user = null) {
        Logger log = Logger.getLogger("")

        def _user = user == null ? ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() : user

        def appLinkService = ComponentAccessor.getComponent(ApplicationLinkService)
        def appLink = appLinkService.getPrimaryApplicationLink(ConfluenceApplicationType)
            def pageIds = (prdUrl =~ /pageId=(\d+)/)[-1] as ArrayList

        def pageId = pageIds[-1]

        //this will be the issue that the remote link will get created
        def linkBuilder = new RemoteIssueLinkBuilder()
        linkBuilder.globalId("appId=" + appLink.getId() + "&pageId=" + pageId)
        linkBuilder.issueId(issue.id)
        linkBuilder.applicationName(appLink.getName())
        linkBuilder.applicationType("com.atlassian.confluence")
        linkBuilder.relationship("Wiki Page")
        linkBuilder.title("Documents")
        linkBuilder.url(prdUrl)
        def link = linkBuilder.build()

        def remoteIssueLinkService = ComponentAccessor.getComponent(RemoteIssueLinkService)
        def validationResult = remoteIssueLinkService.validateCreate(_user, link)

        if (!validationResult.isValid()) {
            log.info validationResult.dump()
            return false;
        }

        remoteIssueLinkService.create(_user, validationResult)
        log.info "Remote link created"
        return true;
    }
  
    static boolean checkGitlabLink(String gitHostUrl, String gitApiVersion, String privateToken, String repositoryUrl, String branch) {
        def repository = (repositoryUrl =~ /${gitHostUrl}\/([\s\S]*).git/)[-1] as ArrayList
        repository = repository[-1] as String
        repository = URLEncoder.encode(repository, "UTF-8")

        def url = "${gitHostUrl}/${gitApiVersion}/projects/${repository}/repository/branches?private_token=${privateToken}&per_page=100"
        def branchList = new URL(url)
        def branches = new groovy.json.JsonSlurper().parse(branchList.newReader())
        branches.any {
          it.getAt("name") == branch
        }
    }

    static boolean createGitlabLink(Issue issue, String repository, String branch, ApplicationUser user = null) {
        Logger log = Logger.getLogger("")

        def _user = user == null ? ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() : user

        def repositoryUrls = (repository =~ /([\s\S]*).git/)[-1] as ArrayList
        def repositoryUrl = repositoryUrls[-1] as String;
        def gitlabUrl = repositoryUrl + "/tree/" + branch

        def links = getRemoteIssueLinks(issue, _user);
        def linkExist = links.any { link -> link.url == gitlabUrl }
        if(linkExist) {
            log.info "Gitlab link has already existed"
            return false
        }

        def repositoryName = repositoryUrl.substring(repositoryUrl.lastIndexOf("/") + 1, repositoryUrl.length())

        //this will be the issue that the remote link will get created
        def linkBuilder = new RemoteIssueLinkBuilder()
        linkBuilder.issueId(issue.id)
        linkBuilder.applicationName("Gitlab")
        linkBuilder.relationship("Source Code")
        linkBuilder.title("Gitlab（Repository：" + repositoryName + " Branch：" + branch + "）")
        linkBuilder.iconUrl("https://www.gitlab.com/favicon.ico")
        linkBuilder.url(gitlabUrl)

        def link = linkBuilder.build()
        def remoteIssueLinkService = ComponentAccessor.getComponent(RemoteIssueLinkService)
        def validationResult = remoteIssueLinkService.validateCreate(_user, link)

        if (!validationResult.isValid()) {
            log.info validationResult.dump()
            return false
        }

        remoteIssueLinkService.create(_user, validationResult)
        log.info "Remote link created"
        return true
    }

    static boolean inStatus(List<String> statuses, Issue issue) {
        return issue.status.name in statuses
    }

    static boolean hasSubtaskInType(Issue issue, String type) {
        def rootIssue = issue.parentObject == null ? issue : issue.parentObject
        return rootIssue.subTaskObjects.any { it.issueType.name == type }
    }

    static boolean allSubtasksInTypeDone(Issue issue, String type) {
        def rootIssue = issue.parentObject == null ? issue : issue.parentObject
        return !rootIssue.subTaskObjects.any { it.issueType.name == type && it.resolution == null }
    }

    static boolean allSubtasksInTypeIsStatus(Issue issue, String type, List<String> status) {
        def rootIssue = issue.parentObject == null ? issue : issue.parentObject
        !rootIssue.subTaskObjects.any {
            it.issueType.name == type && !(it.status.name in status)
        }
    }
}
