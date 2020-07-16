package io.alanda.comments.plugin;

import java.util.UUID;

import javax.inject.Named;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Petra Bierleutgeb, pb@petrabierleutgeb.io
 */
@Named
public class CommentsKeyExecutionListener implements ExecutionListener {

  private static final Logger log = LoggerFactory.getLogger(CommentsKeyExecutionListener.class);

  public static final String COMMENT_KEY_VAR = "commentKey";

  public static final String PROCESS_PACKAGE_KEY_VAR = "processPackageKey";

  public static final String PARENT_PROCESS_PACKAGE_KEY_VAR = "parentProcessPackageKey";

  public static final String CREATE_NEW_PROCESS_PACKAGE_KEY_VAR = "createNewProcessPackageKey";

  public CommentsKeyExecutionListener() {
  }

  @Override
  public void notify(DelegateExecution execution) throws Exception {
    String processInstanceId = execution.getProcessInstanceId();
    RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();
    ExecutionEntity executionEntity = (ExecutionEntity) execution;
    String superProcessInstanceId = null;
    if (executionEntity.getSuperExecution() != null) {
      superProcessInstanceId = executionEntity.getSuperExecution().getProcessInstanceId();
    }
    commentKey(processInstanceId, superProcessInstanceId, runtimeService);
    processPackageKey(processInstanceId, superProcessInstanceId, runtimeService);
  }

  private void commentKey(String processInstanceId, String superProcessInstanceId, RuntimeService runtimeService) {
    String commentKey = (String) runtimeService.getVariable(processInstanceId, COMMENT_KEY_VAR);
    if (commentKey == null) {
      if (superProcessInstanceId != null) {
        String parentCommentKey = (String) runtimeService.getVariable(superProcessInstanceId, COMMENT_KEY_VAR);
        if (parentCommentKey != null) {
          runtimeService.setVariable(processInstanceId, COMMENT_KEY_VAR, parentCommentKey);
        }
      } else {
        String generatedCommentKey = UUID.randomUUID().toString();
        runtimeService.setVariable(processInstanceId, COMMENT_KEY_VAR, generatedCommentKey);
      }
    }
  }

  private void processPackageKey(String processInstanceId, String superProcessInstanceId, RuntimeService runtimeService) {
    String processPackageKey = (String) runtimeService.getVariable(processInstanceId, PROCESS_PACKAGE_KEY_VAR);

    if (processPackageKey == null) {

      if (superProcessInstanceId != null) {
        String parentProcessPackageKey = (String) runtimeService.getVariable(superProcessInstanceId, PROCESS_PACKAGE_KEY_VAR);
        Boolean createNewProcessPackageKey = (Boolean) runtimeService.getVariable(superProcessInstanceId, CREATE_NEW_PROCESS_PACKAGE_KEY_VAR);
        // Checken ob neues ProvessPackage zu erstellen ist
        if (createNewProcessPackageKey != null && createNewProcessPackageKey) {
          runtimeService.setVariable(processInstanceId, PROCESS_PACKAGE_KEY_VAR, processInstanceId);
          if (parentProcessPackageKey != null) {
            runtimeService.setVariable(processInstanceId, PARENT_PROCESS_PACKAGE_KEY_VAR, parentProcessPackageKey);
          }
          runtimeService.removeVariable(processInstanceId, CREATE_NEW_PROCESS_PACKAGE_KEY_VAR);
        } else if (parentProcessPackageKey != null) {
          runtimeService.setVariable(processInstanceId, PROCESS_PACKAGE_KEY_VAR, parentProcessPackageKey);
        } else {
          runtimeService.setVariable(processInstanceId, PROCESS_PACKAGE_KEY_VAR, processInstanceId);
        }
      } else {
        runtimeService.setVariable(processInstanceId, PROCESS_PACKAGE_KEY_VAR, processInstanceId);
      }
    }
  }

}
