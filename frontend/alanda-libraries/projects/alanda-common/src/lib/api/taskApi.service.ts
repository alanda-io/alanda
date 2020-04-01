import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { AlandaTask } from './models/task';
import { catchError } from 'rxjs/operators';
import { AlandaUser } from './models/user';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { ServerOptions } from '../models/serverOptions';

@Injectable()
export class AlandaTaskApiService extends AlandaExceptionHandlingService {

  private endpointUrl: string;

  constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/pmc-task';
  }

  getTask(taskId: string): Observable<AlandaTask> {
    return this.http.get<AlandaTask>(this.endpointUrl + `/${taskId}`)
    .pipe(catchError(this.handleError<AlandaTask>('getTask')));
  }

  loadTasks(serverOptions: ServerOptions): Observable<AlandaTask[]> {
    return this.http.post<AlandaTask[]>(this.endpointUrl + '/list', serverOptions)
    .pipe(catchError(this.handleError<AlandaTask[]>('loadTasks')));
  }

  getCandidates(taskId: string): Observable<AlandaUser[]> {
    return this.http.get<AlandaUser[]>(this.endpointUrl + `/${taskId}` + '/candidates')
    .pipe(catchError(this.handleError<AlandaUser[]>('getCandidates')));
  }

  unclaim(taskId: string): Observable<void> {
    return this.http.post<void>(this.endpointUrl + `/${taskId}` + '/unclaim', {})
    .pipe(catchError(this.handleError<void>('unclaim')));
  }

  assign(taskId: string, userId): Observable<void> {
    return this.http.post<void>(this.endpointUrl + `/${taskId}` + '/assignee', {guid: userId})
    .pipe(catchError(this.handleError<void>('assign')));
  }

  updateTaskDueDate(taskId: string, date: string): Observable<void> {
    return this.http.put<void>(this.endpointUrl + `/${taskId}/dueDate`,date).pipe(catchError(this.handleError<void>('updateTaskDueDate')));
  }

  complete(taskId: string): Observable<any> {
    return this.http.post<any>(this.endpointUrl + `/${taskId}/complete`,{}).pipe(catchError(this.handleError<any>('complete')));
  }

  updateDueDateOfTask(taskId: string, dueDate: string): Observable<void> {
    return this.http.put<void>(this.endpointUrl  + `/${taskId}/dueDate`, dueDate)
    .pipe(catchError(this.handleError<void>('updateDueDateOfTask')));
  }

  setVariable(taskId: string, varName: string, data: any): Observable<void> {
    return this.http.put<void>(this.endpointUrl + `/${taskId}/variables/${varName}`, data);
  }

  search(processInstanceId?: string, taskDefinitionKey?: string): Observable<AlandaTask[]> {
    let qParams = '';
    if (processInstanceId) {
      qParams = qParams + `?processInstanceId=${processInstanceId}`;
    }
    if (taskDefinitionKey) {
      qParams = qParams + `?taskDefinitionKey=${taskDefinitionKey}`;
    }
    return this.http.get<AlandaTask[]>(this.endpointUrl + '/search' + qParams);
  }
}
