import { AlandaProject } from '../shared/api/models/project';
import { AlandaTask } from '../shared/api/models/task';

export interface BaseState {
  project: AlandaProject;
  task: AlandaTask;
}
