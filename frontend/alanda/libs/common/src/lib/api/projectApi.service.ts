import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { AlandaProject, AlandaProjectListData } from './models/project';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ServerOptions } from '../models/serverOptions';
import { AlandaListResult } from './models/listResult';
import { AlandaProcess } from './models/process';
import { AlandaProjectType } from './models/projectType';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { AlandaRefObject } from './models/refObject';
import { AlandaProcessesAndTasks } from './models/processesAndTasks';
import { AlandaSimplePhase } from './models/simplePhase';

@Injectable({
  providedIn: 'root',
})
export class AlandaProjectApiService extends AlandaExceptionHandlingService {
  private readonly endpoint: string;
  private readonly refObjectEndpoint: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpoint = config.API_ENDPOINT + '/project';
    this.refObjectEndpoint = config.API_ENDPOINT + '/refobjects';
  }

  public getProjectByGuid(
    guid: number,
    tree: boolean = false,
  ): Observable<AlandaProject> {
    const params = new HttpParams().set('tree', '' + tree);
    return this.http
      .get<AlandaProject>(`${this.endpoint}/guid/${guid}`, { params })
      .pipe(catchError(this.handleError<AlandaProject>('getProjectByGuid')));
  }

  public getProjectByProjectId(id: string): Observable<AlandaProject> {
    return this.http
      .get<AlandaProject>(`${this.endpoint}/${id}`)
      .pipe(
        catchError(this.handleError<AlandaProject>('getProjectByProjectId')),
      );
  }

  public loadProjects(
    serverOptions: ServerOptions,
  ): Observable<AlandaListResult<AlandaProjectListData>> {
    return this.http
      .post<AlandaListResult<AlandaProjectListData>>(
        `${this.endpoint}/projectsel`,
        serverOptions,
      )
      .pipe(
        catchError(
          this.handleError<AlandaListResult<AlandaProjectListData>>(
            'loadProjects',
          ),
        ),
      );
  }

  public updateProjectDetails(projectId: string, details: string) {
    return this.http
      .put<AlandaProject>(`${this.endpoint}/${projectId}/details`, details)
      .pipe(
        catchError(this.handleError<AlandaProject>('updateProjectHighlight')),
      );
  }

  public updateProjectHighlight(projectId: string, highlight: boolean) {
    return this.http
      .post<AlandaProject>(
        `${this.endpoint}/project/${projectId}/highlight`,
        highlight,
      )
      .pipe(
        catchError(this.handleError<AlandaProject>('updateProjectHighlight')),
      );
  }

  public getProjectHighlight(projectId: string) {
    return this.http
      .get<boolean>(`${this.endpoint}/project/${projectId}/highlight`)
      .pipe(catchError(this.handleError<boolean>('getProjectHighlight')));
  }

  public updateProject(project): Observable<AlandaProject> {
    return this.http
      .put<AlandaProject>(`${this.endpoint}/${project.projectId}`, project)
      .pipe(catchError(this.handleError<AlandaProject>('updateProject')));
  }

  public getProjectMainProcess(projectGuid: number): Observable<AlandaProcess> {
    return this.http
      .get<AlandaProcess>(`${this.endpoint}/project/${projectGuid}/mainprocess`)
      .pipe(
        catchError(this.handleError<AlandaProcess>('getProjectMainProcess')),
      );
  }

  public searchCreateAbleProjectType(
    searchTerm?: string,
  ): Observable<AlandaProjectType[]> {
    let params = new HttpParams();
    if (searchTerm) {
      params = params.set('search', searchTerm);
    } else {
      params = params.set('search', '');
    }
    return this.http
      .get<AlandaProjectType[]>(`${this.endpoint}/createabletype`, {
        params,
      })
      .pipe(catchError(this.handleError('searchCreateableProjectType', [])));
  }

  public getProjectTypeByName(name): Observable<AlandaProjectType> {
    return this.http
      .get<AlandaProjectType>(`${this.endpoint}/project-type-by-name/${name}`)
      .pipe(catchError(this.handleError('getProjectTypeByName', null)));
  }

  public createProject(project: AlandaProject): Observable<AlandaProject> {
    return this.http
      .post<AlandaProject>(`${this.endpoint}/create`, project)
      .pipe(catchError(this.handleError<AlandaProject>('createProject')));
  }

  public getAllProjectProcesses(
    projectGuid: number,
  ): Observable<AlandaProcess[]> {
    return this.http
      .get<AlandaProcess[]>(`${this.endpoint}/project/${projectGuid}/process`)
      .pipe(
        catchError(this.handleError<AlandaProcess[]>('getAllProjectProcesses')),
      );
  }

  public getProjectTreeByGuid(guid: number): Observable<AlandaProject> {
    return this.http
      .get<AlandaProject>(`${this.endpoint}/guid/${guid}?tree=true`)
      .pipe(
        catchError(this.handleError<AlandaProject>('getProjectTreeByGuid')),
      );
  }

  public getProcessesAndTasksForProject(
    guid: number,
  ): Observable<AlandaProcessesAndTasks> {
    return this.http
      .get<AlandaProcessesAndTasks>(
        `${this.endpoint}/project/${guid}/processes-and-tasks`,
      )
      .pipe(
        catchError(
          this.handleError<AlandaProcessesAndTasks>(
            'getProcessesAndTasksForProject',
          ),
        ),
      );
  }

  public autocompleteRefObjects(
    searchTerm: string,
    objectType: string,
  ): Observable<AlandaRefObject[]> {
    const type = objectType.toLowerCase();
    return this.http.get<AlandaRefObject[]>(
      `${this.refObjectEndpoint}/${type}?search=${searchTerm}`,
    );
  }

  public stopProject(
    projectGuid: number,
    reason?: string,
  ): Observable<AlandaProject> {
    let url = `${this.endpoint}/project/${projectGuid}/stop`;
    if (reason) {
      url += `?reason=${reason}`;
    }
    return this.http.get<AlandaProject>(url);
  }

  public getChildTypes(idName: string): Observable<AlandaProjectType[]> {
    return this.http.get<AlandaProjectType[]>(
      `${this.endpoint}/type/${idName}/child-types`,
    );
  }

  public getParentTypes(idName: string): Observable<AlandaProjectType[]> {
    return this.http.get<AlandaProjectType[]>(
      `${this.endpoint}/type/${idName}/parent-types`,
    );
  }

  public updateProjectRelations(
    projectId: string,
    additionalChildren: string,
    removeChildren: string,
    additionalParents: string,
    removeParents: string,
  ): Observable<AlandaProject> {
    let queryString = '';
    if (additionalChildren) {
      queryString += 'additional-children=' + additionalChildren + '&';
    }
    if (removeChildren) {
      queryString += 'remove-children=' + removeChildren + '&';
    }
    if (additionalParents) {
      queryString += 'additional-parents=' + additionalParents + '&';
    }
    if (removeParents) {
      queryString += 'remove-parents=' + removeParents + '&';
    }
    if (queryString.length > 1) {
      queryString = '?' + queryString.substring(0, queryString.length - 1);
    }
    return this.http.put<AlandaProject>(
      `${this.endpoint}/${projectId}/update-relations${queryString}`,
      {},
    );
  }

  public saveProjectProcess(
    projectGuid: number,
    process: AlandaProcess,
  ): Observable<AlandaProcess> {
    return this.http.post<AlandaProcess>(
      `${this.endpoint}/project/${projectGuid}/process`,
      process,
    );
  }

  public startProjectProcess(
    projectGuid: number,
    processGuid: number,
  ): Observable<AlandaProcess> {
    return this.http.get<AlandaProcess>(
      `${this.endpoint}/project/${projectGuid}/process/${processGuid}/start`,
    );
  }

  public stopProjectProcess(
    projectGuid: number,
    processGuid: number,
    reason,
  ): Observable<AlandaProcess> {
    if (reason) {
      return this.http.get<AlandaProcess>(
        `${this.endpoint}/project/${projectGuid}/process/${processGuid}/stop?reason=${reason}`,
      );
    }
    return this.http.get<AlandaProcess>(
      `${this.endpoint}/project/${projectGuid}/process/${processGuid}/stop`,
    );
  }

  public removeProjectProcess(
    projectGuid: number,
    processGuid: number,
    reason,
  ): Observable<any> {
    if (reason) {
      return this.http.delete<any>(
        `${this.endpoint}/project/${projectGuid}/process/${processGuid}?reason=${reason}`,
      );
    }
    return this.http.delete<any>(
      `${this.endpoint}/project/${projectGuid}/process/${processGuid}`,
    );
  }

  public getPhasesForProject(
    projectGuid: number,
  ): Observable<AlandaSimplePhase[]> {
    return this.http.get<AlandaSimplePhase[]>(
      `${this.endpoint}/project/${projectGuid}/phase`,
    );
  }

  public getPhase(
    projectGuid: number,
    phaseDefIdName: string,
  ): Observable<AlandaSimplePhase> {
    return this.http.get<AlandaSimplePhase>(
      `${this.endpoint}/project/${projectGuid}/phase-definition/${phaseDefIdName}`,
    );
  }

  public setPhaseEnabled(
    projectGuid: number,
    phaseDefIdName: string,
    enabled: boolean,
  ): Observable<AlandaSimplePhase> {
    return this.http.post<AlandaSimplePhase>(
      `${this.endpoint}/project/${projectGuid}/phase-definition/${phaseDefIdName}`,
      {
        enabled,
      },
    );
  }

  public restartPhase(
    projectGuid: number,
    phaseDefIdName: string,
  ): Observable<AlandaSimplePhase> {
    return this.http.post<AlandaSimplePhase>(
      `${this.endpoint}/project/${projectGuid}/phase-definition/${phaseDefIdName}/restart`,
      {
        withCredentials: true,
      },
    );
  }

  public startPhase(
    projectGuid: number,
    phaseDefIdName: string,
  ): Observable<AlandaSimplePhase> {
    return this.http.post<AlandaSimplePhase>(
      `${this.endpoint}/project/${projectGuid}/phase-definition/${phaseDefIdName}/start`,
      {
        withCredentials: true,
      },
    );
  }
}
