import { Injectable, Inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { AlandaProperty } from './models/property';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';

@Injectable()
export class AlandaPropertyApiService extends AlandaExceptionHandlingService {
  endpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/pmc-property';
  }

  setDate(
    entityId,
    entityType,
    projectGuid,
    key,
    value,
  ): Observable<AlandaProperty> {
    return this.set(entityId, entityType, projectGuid, key, value, 'DATE');
  }

  setString(
    entityId,
    entityType,
    projectGuid,
    key,
    value,
  ): Observable<AlandaProperty> {
    return this.set(entityId, entityType, projectGuid, key, value, 'STRING');
  }

  /* setGroupRole(projectGuid, key, value): Observable<any> {
        return this.set(null, null, projectGuid, 'role_group_' + key, value, 'STRING');
    }

    setUserRole(projectGuid, key, value): Observable<any> {
        return this.set(null, null, projectGuid, 'role_user_' + key, value, 'STRING');
    }

    getUserRole(projectGuid, key): Observable<any> {
        key = 'role_user_' +  key;
        return this.http.get<any>(`${this.endpointUrl}/get?pmc-project-guid=${projectGuid}&key=${key}'`)
        .pipe(catchError(this.handleError('getUserRole')));
    }

    getGroupRole(projectGuid, key): Observable<any> {
        key = 'role_group_' +  key;
        return this.http.get<any>(`${this.endpointUrl}/get?pmc-project-guid=${projectGuid}&key=${key}'`)
        .pipe(catchError(this.handleError('getGroupRole')));
    } */

  set(
    entityId,
    entityType,
    projectGuid,
    key,
    value,
    valueType,
  ): Observable<AlandaProperty> {
    const obj = {
      entityId,
      entityType,
      pmcProjectGuid: projectGuid,
      key,
      value,
      valueType,
    };
    return this.http
      .post<AlandaProperty>(`${this.endpointUrl}/set`, obj)
      .pipe(catchError(this.handleError<AlandaProperty>('setProperty')));
  }

  get(entityId, entityType, projectGuid, key): Observable<AlandaProperty> {
    let params = new HttpParams().set('key', key);
    if (entityId) {
      params = params.set('entity-id', entityId);
    }
    if (entityType) {
      params = params.set('entity-type', entityType);
    }
    if (projectGuid) {
      params = params.set('pmc-project-guid', projectGuid);
    }
    return this.http
      .get<AlandaProperty>(`${this.endpointUrl}/get`, { params })
      .pipe(catchError(this.handleError<AlandaProperty>('getProperty')));
  }

  getPropertyWithPrefix(
    projectGuid: number,
    prefix: string,
    delimiter?: string,
  ): Observable<AlandaProperty[]> {
    let params = new HttpParams();
    if (delimiter) {
      params = params.set('delim', delimiter);
    }
    return this.http
      .get<AlandaProperty[]>(
        `${this.endpointUrl}/get-with-prefix/${projectGuid}/${prefix}`,
        { params },
      )
      .pipe(
        catchError(this.handleError<AlandaProperty[]>('getPropertyWithPrefix')),
      );
  }

  getPropertiesMap(projectGuid: number): Observable<Map<string, any>> {
    return this.http
      .get<Map<string, any>>(`${this.endpointUrl}/propertiesmap/${projectGuid}`)
      .pipe(catchError(this.handleError<Map<string, any>>('getPropertiesMap')));
  }

  delete(entityId, entityType, pmcProjectGuid, key): Observable<void> {
    let params = new HttpParams().set('key', key);
    if (entityId) {
      params = params.set('entity-id', entityId);
    }
    if (entityType) {
      params = params.set('entity-type', entityType);
    }
    if (pmcProjectGuid) {
      params = params.set('pmc-project-guid', pmcProjectGuid);
    }
    return this.http
      .delete<void>(`${this.endpointUrl}/delete`, { params })
      .pipe(catchError(this.handleError<void>('deleteProperty')));
  }
}
