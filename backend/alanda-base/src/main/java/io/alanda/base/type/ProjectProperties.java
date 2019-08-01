package io.alanda.base.type;

public class ProjectProperties {

  public static final String STARTING_PARENT_PROJECT_ID = "starting-parent-project-id";

  /**
   * marker property for batch created projects
   */
  public static final String CREATED_BY_BATCH = "createdByBatch";

  /**
   * property to mark that the optional finalization step of the project should be skipped
   */
  public static final String SKIP_PROJECT_FINALIZATION = "skipProjectFinalization";

  /**
   * property to mark that the initial checks step of the project should be skipped
   */
  public static final String SKIP_PROJECT_INITIAL_CHECKS = "skipProjectInitialChecks";

}
