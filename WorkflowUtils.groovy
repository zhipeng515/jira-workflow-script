import com.atlassian.jira.util.ErrorCollection
import com.atlassian.jira.issue.status.Status
import com.atlassian.jira.workflow.WorkflowTransitionUtil;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.component.ComponentAccessor;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowTransitionUtilFactory;

class WorkflowUtils {
    static ActionDescriptor findTransition(Issue issue, String actionNamePattern) {
        JiraWorkflow workflow = ComponentAccessor.getWorkflowManager().getWorkflow(issue)
        Status status = issue.getStatus()
        StepDescriptor sd = workflow.getLinkedStep(status)
        Iterator<ActionDescriptor> it = sd.getActions().iterator()
        ActionDescriptor action = null
        while ( it.hasNext() ) {
            ActionDescriptor ad = (ActionDescriptor)it.next()
            String actionName = ad.getName()
            if ( actionName==actionNamePattern ) {
                action = ad
            }
        }
        return action
    }

    static void executeTransition(Issue issue, ActionDescriptor action, ApplicationUser user = null) {
      try {
            def _user = user == null ? ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser() : user
            WorkflowTransitionUtil workflowTransitionUtil = ComponentAccessor.getComponent(WorkflowTransitionUtilFactory.class).create()
            ErrorCollection errors = null
            workflowTransitionUtil.setIssue((MutableIssue) issue)
            workflowTransitionUtil.setUserkey(_user?.getKey())
            workflowTransitionUtil.setAction(action?.getId())
        	workflowTransitionUtil.progress()
            errors = workflowTransitionUtil.validate()
        } catch (Exception e) {
        }
    }

    static void executeTransition(Issue issue, String actionNamePattern, ApplicationUser user = null) {
        def action = findTransition(issue, actionNamePattern);
        executeTransition(issue, action, user);
    }
}
