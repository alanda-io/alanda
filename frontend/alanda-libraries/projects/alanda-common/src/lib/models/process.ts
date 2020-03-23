import { ProjectState } from "../enums/project-status.enum";
import { ProcessRelation } from "../enums/process-relation.enum";
import { PmcTask } from "./pmcTask";
import { ProcessResultStatus } from "../enums/process-result-status.enum";

export class Process {
  public guid: number;
  public version: number;
  public processInstanceId: string;
  public parentExecutionId: string;
  public status: ProjectState;
  public relation: ProcessRelation;
  public workDetails: string;
  public processKey: string;
  public businessObject: string;
  public label: string;
  public phase: string;
  public tasks: PmcTask[];
  public resultStatus: ProcessResultStatus
  public resultComment: string;
  public customRefObject: boolean;
  public startTime: Date;
  public endTime: Date;
  public processDefinitionId: string;
  public activeOrSuspended: boolean;
}

export function isProcess(p: any): p is Process {
  return p.processInstanceId !== undefined;
}
