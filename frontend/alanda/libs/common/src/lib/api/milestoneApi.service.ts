import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { AlandaProjectMilestone } from './models/projectMilestone';

@Injectable({
  providedIn: 'root',
})
export class AlandaMilestoneApiService extends AlandaExceptionHandlingService {
  private readonly endpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/ms';
  }

  getByProjectAndMsIdName(
    projectId: string,
    msIdName: string,
  ): Observable<AlandaProjectMilestone> {
    return this.http
      .get<any>(`${this.endpointUrl}/project/${projectId}/ms/${msIdName}`)
      .pipe(catchError(this.handleError('getByProjectAndMsIdName')));
  }

  updateByProjectAndMsIdName(
    projectId: string,
    msIdName: string,
    fc: string,
    act: string,
    reason: string,
    delFc: boolean,
    delAct: boolean,
  ): Observable<void> {
    let url = `${this.endpointUrl}/project/${projectId}/ms/${msIdName}?delFc=${delFc}&delAct=${delAct}`;
    if (reason) {
      url = `${url}&reason=${reason}`;
    }
    return this.http
      .put<void>(url, { act, fc })
      .pipe(catchError(this.handleError<void>('updateByProjectAndMsIdName')));
  }
}
