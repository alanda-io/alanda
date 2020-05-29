import { AlandaProject } from '../api/models/project';
import { AlandaTask } from '../api/models/task';

export interface BaseState {
  project: AlandaProject
  task: AlandaTask
}
