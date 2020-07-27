import { AlandaProcess } from './process';

export interface ProcessesResponse {
  processName?: string;
  processDefinitionKey?: string;
}

export interface AlandaProcessesAndTasks {
  phaseNames?: Map<string, string>;
  allowed?: Map<string, ProcessesResponse[]>;
  active?: AlandaProcess[];
}
