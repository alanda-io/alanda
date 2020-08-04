import {
  AbstractControl,
  AsyncValidatorFn,
  ValidationErrors,
} from '@angular/forms';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AlandaProject, AlandaProjectApiService } from '../..';

export function subProcessValidator(
  project: AlandaProject,
  projectService: AlandaProjectApiService,
  phaseIdName: string = null,
  valuesToCheck: any[] = [],
  checkForAllProcessesFinished: boolean = true,
  checkForStartedProcesses: string[] = [],
  checkForActivePhaseProcesses: boolean = false,
): AsyncValidatorFn {
  interface ActiveProcess {
    guid: number;
    processInstanceId: string;
    parentExecutionId: string;
    status: string;
    relation: string;
    workDetails: string;
    processKey: string;
    businessObject: string;
    label: string;
    phase: string;
    tasks: object[];
    resultStatus: string;
    resultComment: string;
    customRefObject: string;
    startTime: string;
    endTime: string;
    processDefinitionId: string;
    activeOrSuspended: boolean;
  }

  return (control: AbstractControl): Observable<ValidationErrors> | null => {
    return projectService.getProcessesAndTasksForProject(project.guid).pipe(
      map((ret: any) => {
        if (!valuesToCheck.includes(control.value)) {
          return null;
        }

        const activeProcesses: ActiveProcess[] = ret.active;

        if (checkForAllProcessesFinished) {
          for (const proc of activeProcesses) {
            if (phaseIdName !== null && phaseIdName !== proc.phase) {
              continue;
            }
            if (
              proc.relation === 'CHILD' &&
              (proc.status === 'ACTIVE' || proc.status === 'SUSPENDED')
            ) {
              return {
                stillActiveProcesses: {
                  text: 'At least one sub process is still active!',
                },
              };
            }
          }
        }

        if (checkForActivePhaseProcesses) {
          let valid = false;
          for (const proc of activeProcesses) {
            if (phaseIdName !== null && phaseIdName !== proc.phase) {
              continue;
            }
            if (
              proc.relation === 'CHILD' &&
              (proc.status === 'ACTIVE' || proc.status === 'SUSPENDED') &&
              phaseIdName !== null &&
              phaseIdName === proc.phase
            ) {
              valid = true;
              break;
            }
          }
          if (!valid) {
            return {
              noActiveProcess: {
                text:
                  "No sub processes are running in this phase. You can't wait further.",
              },
            };
          }
        }

        for (const processToStart of checkForStartedProcesses) {
          let started = false;
          for (const proc of activeProcesses) {
            if (
              proc.processKey === processToStart &&
              (proc.status === 'ACTIVE' || proc.status === 'SUSPENDED')
            ) {
              started = true;
              break;
            }
          }
          if (!started) {
            return {
              processNotStarted: {
                text:
                  "Can't continue until process with key " +
                  processToStart +
                  ' has been started!',
              },
            };
          }
        }
        return null;
      }),
    );
  };
}
