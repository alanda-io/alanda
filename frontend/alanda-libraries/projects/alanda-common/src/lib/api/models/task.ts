import { ProjectState } from '../../enums/projectState.enum';

export interface AlandaTask {
  assignee?: string;
  created?: string;
  due?: string;
  formKey?: string;
  priority?: number;
  description?: string;
  comment?: string;
  pmcProjectGuid?: number;
  candidateGroups?: string[];
  candidateGroupIds?: number[];
  state?: ProjectState;
  task_id?: string;
  task_type?: string;
  task_name?: string;
  object_name?: string;
  object_id?: number;
  assignee_id?: string;
  execution_id?: string;
  follow_up?: string;
  process_definition_id?: string;
  process_instance_id?: string;
  process_name?: string;
  process_definition_key?: string;
  process_package_key?: string;
  suspension_state?: boolean;
  actinst_type?: string;
}
