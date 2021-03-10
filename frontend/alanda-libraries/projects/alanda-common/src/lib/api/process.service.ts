import { Injectable, Inject } from "@angular/core";
import { ExceptionHandlingService } from "../services/exception-handling.service";
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { AppSettings, APP_CONFIG } from "../models/appSettings";

@Injectable({
    providedIn: 'root'
  })
  export class ProcessServiceNg extends ExceptionHandlingService {

    endpointUrl: string;

    constructor(private http: HttpClient,
        @Inject(APP_CONFIG) config: AppSettings) {
        super();
        this.endpointUrl = config.API_ENDPOINT + "/pmc-process";
      }

    getVariable(processInstanceId, variableName): Observable<any> {
      return this.http.get(`${this.endpointUrl}/${processInstanceId}/variables/${variableName}`, {}).pipe(catchError(this.handleError('getVariable', null)));
    }

    queryProcess(query: string): Observable<string[]> {
      const params: HttpParams = new HttpParams().set('query', query);
      return this.http.get<string[]>(`${this.endpointUrl}`, {params}).pipe(catchError(this.handleError('queryProcess', null)));
    }

  queryUserTasks(processDefKey: string, query: string): Observable<string[]> {
    const params: HttpParams = new HttpParams().set('query', query);
    return this.http.get<string[]>(`${this.endpointUrl}/${processDefKey}/tasks`, {params}).pipe(catchError(this.handleError('queryUserTasks', null)));
  }
}
