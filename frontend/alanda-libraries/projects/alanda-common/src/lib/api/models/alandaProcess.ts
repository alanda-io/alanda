import { AlandaTask } from './alandaTask';
import { ProcessResultStatus } from '../../enums/processResultStatus.enum';
import { ProcessRelation } from '../../enums/processRelation.enum';
import { ProjectState } from '../../enums/projectState.enum';

export interface AlandaProcess {
  guid?: number;
  version?: number;
  processInstanceId?: string;
  parentExecutionId?: string;
  status?: ProjectState;
  relation?: ProcessRelation;
  workDetails?: string;
  processKey?: string;
  businessObject?: string;
  label?: string;
  phase?: string;
  tasks?: AlandaTask[];
  resultStatus?: ProcessResultStatus;
  resultComment?: string;
  customRefObject?: boolean;
  startTime?: Date;
  endTime?: Date;
  processDefinitionId?: string;
  activeOrSuspended?: boolean;
}

