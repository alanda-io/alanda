import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { AlandaTask } from './models/task';
import { catchError } from 'rxjs/operators';
import { AlandaUser } from './models/user';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { ServerOptions } from '../models/serverOptions';
import { AlandaListResult } from './models/listResult';

@Injectable({
  providedIn: 'root',
})
export class AlandaTaskApiService extends AlandaExceptionHandlingService {
  private readonly endpointUrl: string;
  private readonly processEndpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/pmc-task';
    this.processEndpointUrl = config.API_ENDPOINT + '/pmc-process';
  }

  getTask(taskId: string): Observable<AlandaTask> {
    return this.http
      .get<AlandaTask>(this.endpointUrl + `/${taskId}`)
      .pipe(catchError(this.handleError<AlandaTask>('getTask')));
  }

  loadTasks(
    serverOptions: ServerOptions,
  ): Observable<AlandaListResult<AlandaTask>> {
    return this.http
      .post<AlandaListResult<AlandaTask>>(
        this.endpointUrl + '/list',
        serverOptions,
      )
      .pipe(
        catchError(this.handleError<AlandaListResult<AlandaTask>>('loadTasks')),
      );
  }

  getCandidates(taskId: string): Observable<AlandaUser[]> {
    return this.http
      .get<AlandaUser[]>(this.endpointUrl + `/${taskId}` + '/candidates')
      .pipe(catchError(this.handleError<AlandaUser[]>('getCandidates')));
  }

  unclaim(taskId: string): Observable<void> {
    return this.http
      .post<void>(this.endpointUrl + `/${taskId}` + '/unclaim', {})
      .pipe(catchError(this.handleError<void>('unclaim')));
  }

  assign(taskId: string, userId): Observable<void> {
    return this.http
      .post<void>(this.endpointUrl + `/${taskId}` + '/assignee', {
        guid: userId,
      })
      .pipe(catchError(this.handleError<void>('assign')));
  }

  updateTaskDueDate(taskId: string, date: string): Observable<void> {
    return this.http
      .put<void>(this.endpointUrl + `/${taskId}/dueDate`, date)
      .pipe(catchError(this.handleError<void>('updateTaskDueDate')));
  }

  complete(taskId: string): Observable<any> {
    return this.http
      .post<any>(this.endpointUrl + `/${taskId}/complete`, {})
      .pipe(catchError(this.handleError<any>('complete')));
  }

  updateDueDateOfTask(taskId: string, dueDate: string): Observable<void> {
    const headers = new HttpHeaders().set('content-type', 'application/json');
    return this.http
      .put<void>(this.endpointUrl + `/${taskId}/dueDate`, dueDate, { headers })
      .pipe(catchError(this.handleError<void>('updateDueDateOfTask')));
  }

  setVariable(taskId: string, varName: string, data: any): Observable<void> {
    return this.http.put<void>(
      this.endpointUrl + `/${taskId}/variables/${varName}`,
      data,
    );
  }

  getVariable(taskId: string, varName: string): Observable<any> {
    return this.http.get<any>(
      this.endpointUrl + `/${taskId}/variables/${varName}`,
    );
  }

  snoozeTask(taskId: string, days: number): Observable<any> {
    return this.http.put(this.endpointUrl + `/${taskId}/snooze`, days);
  }

  search(
    processInstanceId?: string,
    taskDefinitionKey?: string,
  ): Observable<AlandaTask[]> {
    let qParams = new HttpParams();
    if (processInstanceId) {
      qParams = qParams.set('processInstanceId', processInstanceId);
    }
    if (taskDefinitionKey) {
      qParams = qParams.set('taskDefinitionKey', taskDefinitionKey);
    }
    return this.http.get<AlandaTask[]>(this.endpointUrl + '/search', {
      params: qParams,
    });
  }

  getProcessVariable(processInstanceId, variableName): Observable<any> {
    return this.http
      .get(
        `${this.processEndpointUrl}/${processInstanceId}/variables/${variableName}`,
        {},
      )
      .pipe(catchError(this.handleError('getProcessVariable', null)));
  }
}
