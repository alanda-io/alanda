import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlandaProcess } from './models/process';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';

@Injectable()
export class AlandaProcessApiService extends AlandaExceptionHandlingService {
  endpointUrl: string;

  constructor (private readonly http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/process';
  }

  getProcessInfoForProcessInstanceId (processInstanceId, processPackageKey): Observable<AlandaProcess> {
    return this.http.get<AlandaProcess>(`${this.endpointUrl}/info/${processInstanceId}/${processPackageKey}`)
      .pipe(catchError(this.handleError('getProcessInfoForProcessInstanceId', null)));
  }

  getProcessInfoForProcessInstance (processInstanceId): Observable<AlandaProcess> {
    return this.http.get<AlandaProcess>(`${this.endpointUrl}/info/${processInstanceId}/`)
      .pipe(catchError(this.handleError('getProcessInfoForProcessInstance', null)));
  }

  saveReqProcess (processInstanceId, reqProcessDto): Observable<AlandaProcess> {
    return this.http.post<AlandaProcess>(`${this.endpointUrl}/reqProcess/${processInstanceId}/save`, reqProcessDto)
      .pipe(catchError(this.handleError('saveReqProcess')));
  }

  startReqProcess (processInstanceId, executionId, index): Observable<AlandaProcess> {
    return this.http.post<AlandaProcess>(`${this.endpointUrl}/reqProcess/${processInstanceId}/#${executionId}/start/#${index}`, {})
      .pipe(catchError(this.handleError('startReqProcess')));
  }

  removeReqProcess (processInstanceId, index): Observable<AlandaProcess> {
    return this.http.post<AlandaProcess>(`${this.endpointUrl}/reqProcess/${processInstanceId}/remove/${index}`, {})
      .pipe(catchError(this.handleError('removeReqProcess')));
  }

  getReqProcessInfo (processInstanceId): Observable<AlandaProcess> {
    return this.http.get<AlandaProcess>(`${this.endpointUrl}/reqProcess/${processInstanceId}/info`, {})
      .pipe(catchError(this.handleError('getReqProcessInfo', null)));
  }
}
