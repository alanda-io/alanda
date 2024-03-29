import { AlandaProcess } from './process';
import { ProjectState } from '../../enums/projectState.enum';
import { AlandaMilestone } from './milestone';
import { AlandaSimplePhase } from './simplePhase';
import { AlandaHistoryLog } from './historyLog';
import { AlandaProperty } from './property';
import { AlandaProjectType } from './projectType';

export interface AlandaProject {
  guid?: number;
  version?: number;
  projectId?: string;
  tag?: string[];
  title?: string;
  status?: ProjectState;
  priority?: number;
  refObjectIdName?: string;
  refObjectDisplayName?: string;
  refObjectType?: string;
  projectType?: string;
  projectTypeIdName?: string;
  subtype?: string; // intentionally no camelcase to match the backend!
  dueDate?: string;
  pmcProjectType?: AlandaProjectType;
  parents?: AlandaProject[];
  children?: AlandaProject[];
  processes?: AlandaProcess[];
  customerProjectId?: number;
  ownerId?: number;
  details?: string;
  guStatus?: string;
  comment?: string;
  risk?: number;
  refObjectId?: number;
  milestones?: AlandaMilestone[];
  phases?: AlandaSimplePhase[];
  history?: AlandaHistoryLog[];
  displayMetaInfo?: string;
  properties?: AlandaProperty[];
  propertiesMap?: Map<string, any>;
  milestonesMap?: Map<string, any>;
  createDate?: string;
  createUser?: number;
  ownerName?: string;
  updateDate?: string;
  updateUser?: number;
  parentIds?: number[];
  childrenIds?: number[];
  additionalInfo?: Map<string, any>;
  resultStatus?: string;
  resultComment?: string;
  authBase?: string;
  running?: boolean;
  humanReadableId?: string;
  highlighted?: boolean;
}

export interface AlandaProjectListData {
  project: AlandaProject;
}
