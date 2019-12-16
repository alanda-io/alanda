import { Injectable, Inject } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { AppSettings, APP_CONFIG } from "../../models/AppSettings";
import { Observable } from "rxjs";
import { ServerOptions } from "../../models/serverOptions.model";
import { ExceptionHandlingService } from "../exceptionHandling.service";
import { catchError } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class TaskServiceNg extends ExceptionHandlingService{

  private endpointUrl : string;

  constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + "/pmc-task";
  }

  getTask(taskId: string): Observable<any> {
    return this.http.get<any>(this.endpointUrl + `/${taskId}`).pipe(catchError(this.handleError('getTask')));
  }

  loadTasks(serverOptions: ServerOptions): Observable<any[]> {
    return this.http.post<any[]>(this.endpointUrl + '/list',serverOptions).pipe(catchError(this.handleError<any[]>('loadTasks')));
  }

  getCandidates(taskId: string): Observable<any[]>{
    return this.http.get<any[]>(this.endpointUrl + `/${taskId}` + '/candidates').pipe(catchError(this.handleError<any[]>('getCandidates')));
  }

  unclaim(taskId: string):Observable<any>{
    return this.http.post<any>(this.endpointUrl + `/${taskId}` + '/unclaim',{}).pipe(catchError(this.handleError<any>('unclaim')));
  }

  assign(taskId: string, userId): Observable<any>{
    return this.http.post<any>(this.endpointUrl + `/${taskId}` + '/assignee', {guid: userId}).pipe(catchError(this.handleError<any>('assign')));
  }

  updateTaskDueDate(taskId: string, date: string): Observable<void> {
    return this.http.put<void>(this.endpointUrl + `/${taskId}/dueDate`,date).pipe(catchError(this.handleError<void>('updateTaskDueDate')));
  }

  complete(taskId: string): Observable<any> {
    return this.http.post<any>(this.endpointUrl + `/${taskId}/complete`,{}).pipe(catchError(this.handleError<any>('complete')));
  }

  updateDueDateOfTask(taskId: string, dueDate: string): Observable<any> {
    let header = new HttpHeaders();
    header = header.append('content-type', 'application/json');
    return this.http.put<any>(this.endpointUrl  + `/${taskId}/dueDate`, dueDate, {headers: header}).pipe(catchError(this.handleError<any>('updateDueDateOfTask')));
  }

  setVariable(taskId: string, varName: string, data: any): Observable<any> {
    return this.http.put(this.endpointUrl + `/${taskId}/variables/${varName}`, data);
  }
}
