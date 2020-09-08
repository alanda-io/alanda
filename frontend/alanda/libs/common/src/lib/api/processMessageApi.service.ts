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
    const reqUrl = `${this.endpointUrl}/${messageName}/${processDefinitionKey}/${pmcProjectGuid}`;
    let params = {};
    processVariables.forEach((value, key) => {
      params[key] = value;
    });
    return this.http
      .post(reqUrl, params)
      .pipe(catchError(this.handleError('sendMessage')));
  }
}
