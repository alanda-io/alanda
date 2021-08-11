import { AlandaPhaseDefinition } from './phaseDefinition';

export interface AlandaSimplePhase {
  active: boolean;
  createDate?: Date;
  createUser?: number;
  enabled: boolean;
  endDate?: Date;
  frozen?: boolean;
  guid: number;
  pmcProjectPhaseDefinition: AlandaPhaseDefinition;
  startDate?: Date;
  updateDate: Date;
  updateUser: number;
  idName?: string;
}
