import { Injectable, Inject } from '@angular/core';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { HttpClient } from '@angular/common/http';
import { AppSettings, APP_CONFIG } from '../models/appSettings';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AlandaProcessMessageApiService extends AlandaExceptionHandlingService {
  endpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/messages';
  }

  public sendMessage(
    messageName: string,
    processDefinitionKey: string,
    pmcProjectGuid: number,
    processVariables: Map<string, string>,
  ): Observable<any> {
    let reqUrl = `${this.endpointUrl}/${messageName}/${processDefinitionKey}/${pmcProjectGuid}`;
    return this.http
      .post(reqUrl, processVariables)
      .pipe(catchError(this.handleError('sendMessage')));
  }
}
