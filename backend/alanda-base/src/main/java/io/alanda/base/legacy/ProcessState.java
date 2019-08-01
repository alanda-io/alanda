package io.alanda.base.legacy;

public enum ProcessState {
    NEW("new"),
    ACTIVE("active"),
    COMPLETED("completed"),
    CLOSED("closed"),
    SUSPENDED("suspended"),
    CANCELLED("cancelled"),
    DELETED("deleted"),
    ERROR("error"),
    UNKNOWN("unknown"),
    WAITING_INITIAL_DECISION("waiting_initial_decision"),
    WAITING_REEVALUATION("waiting_reevaluation"),
    WAITING_ROLLOUT_DECISION("waiting_rollout_decision"),
    WAITING_COST_EVALUATION("waiting_cost_evaluation");

  private String state;

  private ProcessState(String state) {
    this.state = state;
  }

  public String getState() {
    return state;
  }
}
