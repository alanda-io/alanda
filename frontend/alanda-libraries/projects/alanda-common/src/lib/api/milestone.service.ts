import { ExceptionHandlingService } from "../services/exception-handling.service";
import { Injectable, Inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { AppSettings, APP_CONFIG } from "../models/appSettings";
import { Observable } from "rxjs";
import { catchError } from "rxjs/operators";

@Injectable({
    providedIn: 'root'
  })
  export class MilestoneService extends ExceptionHandlingService{ 

    private endpointUrl: string;

    constructor(private http: HttpClient,
        @Inject(APP_CONFIG) config: AppSettings) {
        super();
        this.endpointUrl = config.API_ENDPOINT + "/ms";
      }

    public getByProjectAndMsIdName(projectId: string, msIdName: string): Observable<any> {
        return this.http.get<any>(`${this.endpointUrl}/project/${projectId}/ms/${msIdName}`).pipe(catchError(this.handleError('getByProjectAndMsIdName')));
    }

    public updateByProjectAndMsIdName(projectId: string, msIdName: string, fc: Date, act: Date, reason: string, delFc: boolean, delAct: boolean) {
        let url = `${this.endpointUrl}/project/${projectId}/ms/${msIdName}?delFc=${delFc}&delAct=${delAct}`;
        if (reason) {
            url = `${url}&reason=${reason}`;
        }
        return this.http.put(url, {act: act, fc: fc});
    }
    
}