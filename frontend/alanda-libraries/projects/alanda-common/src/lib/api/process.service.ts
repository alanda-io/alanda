import { Injectable, Inject } from "@angular/core";
import { ExceptionHandlingService } from "../services/exception-handling.service";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";
import { AppSettings, APP_CONFIG } from "../models/appSettings";

@Injectable({
    providedIn: 'root'
  })
  export class ProcessServiceNg extends ExceptionHandlingService{ 

    endpointUrl: string;

    constructor(private http: HttpClient,
        @Inject(APP_CONFIG) config: AppSettings) {
        super();
        this.endpointUrl = config.API_ENDPOINT + "/process";
      }
 

    getProcessInfoForProcessInstanceId(processInstanceId, processPackageKey): Observable<any> {
        return this.http.get(`${this.endpointUrl}/info/${processInstanceId}/${processPackageKey}`).pipe(catchError(this.handleError('getProcessInfoForProcessInstanceId', null)));
    }   

    getProcessInfoForProcessInstance(processInstanceId): Observable<any> {
        return this.http.get(`${this.endpointUrl}/info/${processInstanceId}/`).pipe(catchError(this.handleError('getProcessInfoForProcessInstance', null)));
    }

    saveReqProcess(processInstanceId, reqProcessDto): Observable<any> {
        return this.http.post(`${this.endpointUrl}/reqProcess/${processInstanceId}/save`, reqProcessDto).pipe(catchError(this.handleError('saveReqProcess')));
    }

    startReqProcess(processInstanceId, executionId, index): Observable<any> {
        return this.http.post(`${this.endpointUrl}/reqProcess/${processInstanceId}/#${executionId}/start/#${index}`, {}).pipe(catchError(this.handleError('startReqProcess')));
    }

    removeReqProcess(processInstanceId, index): Observable<any> {
        return this.http.post(`${this.endpointUrl}/reqProcess/${processInstanceId}/remove/${index}`, {}).pipe(catchError(this.handleError('removeReqProcess')));
    }

    getReqProcessInfo(processInstanceId): Observable<any> {
        return this.http.get(`${this.endpointUrl}/reqProcess/${processInstanceId}/info`, {}).pipe(catchError(this.handleError('getReqProcessInfo', null)));
    }
}
