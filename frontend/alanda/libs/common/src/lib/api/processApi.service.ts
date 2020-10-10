import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlandaProcess } from './models/process';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { AlandaReqProcess } from './models/reqProcess';

@Injectable({
  providedIn: 'root',
})
export class AlandaProcessApiService extends AlandaExceptionHandlingService {
  endpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/process';
  }

  getProcessInfoForProcessInstanceId(
    processInstanceId,
    processPackageKey,
  ): Observable<AlandaProcess[]> {
    return this.http
      .get<AlandaProcess[]>(
        `${this.endpointUrl}/info/${processInstanceId}/${processPackageKey}`,
      )
      .pipe(
        catchError(
          this.handleError('getProcessInfoForProcessInstanceId', null),
        ),
      );
  }

  getProcessInfoForProcessInstance(
    processInstanceId,
  ): Observable<AlandaProcess> {
    return this.http
      .get<AlandaProcess>(`${this.endpointUrl}/info/${processInstanceId}/`)
      .pipe(
        catchError(this.handleError('getProcessInfoForProcessInstance', null)),
      );
  }

  saveReqProcess(processInstanceId, reqProcessDto): Observable<void> {
    return this.http
      .post<void>(
        `${this.endpointUrl}/reqProcess/${processInstanceId}/save`,
        reqProcessDto,
      )
      .pipe(catchError(this.handleError<void>('saveReqProcess')));
  }

  startReqProcess(processInstanceId, executionId, index): Observable<void> {
    return this.http
      .post<void>(
        `${this.endpointUrl}/reqProcess/${processInstanceId}/${executionId}/start/${index}`,
        {},
      )
      .pipe(catchError(this.handleError<void>('startReqProcess')));
  }

  removeReqProcess(processInstanceId, index): Observable<void> {
    return this.http
      .post<void>(
        `${this.endpointUrl}/reqProcess/${processInstanceId}/remove/${index}`,
        {},
      )
      .pipe(catchError(this.handleError<void>('removeReqProcess')));
  }

  getReqProcessInfo(processInstanceId): Observable<AlandaReqProcess[]> {
    return this.http
      .get<AlandaReqProcess[]>(
        `${this.endpointUrl}/reqProcess/${processInstanceId}/info`,
        {},
      )
      .pipe(catchError(this.handleError('getReqProcessInfo', null)));
  }
}
