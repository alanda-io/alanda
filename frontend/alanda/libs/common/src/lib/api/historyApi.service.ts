import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { AlandaHistoryLog } from './models/historyLog';

@Injectable({
  providedIn: 'root',
})
export class AlandaHistoryApiService extends AlandaExceptionHandlingService {
  private readonly endpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT;
  }

  addOrUpdateHistoryLog(
    projectId: string,
    historyLog: AlandaHistoryLog,
  ): Observable<any> {
    return this.http
      .post<AlandaHistoryLog>(
        `${this.endpointUrl}/api/v1/project/${projectId}/history`,
        historyLog,
      )
      .pipe(
        catchError(this.handleError<AlandaHistoryLog>('addOrUpdateHistoryLog')),
      );
  }

  search(filterOptions, pageNumber, pageSize): Observable<any[]> {
    const searchDto = {};
    for (const key of Object.keys(filterOptions)) {
      if (filterOptions[key] !== '') {
        searchDto[key] = encodeURIComponent(filterOptions[key]);
      }
    }
    return this.http
      .post<any[]>(
        `${this.endpointUrl}/history/search/${pageNumber}/${pageSize}`,
        searchDto,
      )
      .pipe(catchError(this.handleError('search', [])));
  }
}
