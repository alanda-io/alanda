import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { AlandaProperty } from './models/alandaProperty';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/alandaExceptionHandling.service';

@Injectable({
    providedIn: 'root'
  })
  export class AlandaPropertyService extends AlandaExceptionHandlingService {

    endpointUrl: string;

    constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
        super();
        this.endpointUrl = config.API_ENDPOINT + '/pmc-property';
      }

    setDate(entityId, entityType, projectGuid, key, value): Observable<AlandaProperty> {
        return this.set(entityId, entityType, projectGuid, key, value, 'DATE');
    }

    setString(entityId, entityType, projectGuid, key, value): Observable<AlandaProperty> {
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

    set(entityId, entityType, projectGuid, key, value, valueType): Observable<AlandaProperty> {
        const obj = {
            entityId: entityId,
            entityType: entityType,
            pmcProjectGuid: projectGuid,
            key: key,
            value: value,
            valueType: valueType
        };
        return this.http.post<AlandaProperty>(`${this.endpointUrl}/set`, obj)
        .pipe(catchError(this.handleError<AlandaProperty>('setProperty')));
    }

    get(entityId, entityType, projectGuid, key): Observable<AlandaProperty> {
        let urlString = '/get?';
        if (entityId) {
            urlString += 'entity-id=' + entityId + '&';
        }
        if (entityType) {
            urlString += 'entity-type=' + entityType + '&';
        }
        if (projectGuid) {
            urlString += 'pmc-project-guid=' + projectGuid + '&';
        }
        urlString += 'key=' + key;
        return this.http.get<AlandaProperty>(`${this.endpointUrl}${urlString}`)
        .pipe(catchError(this.handleError<AlandaProperty>('getProperty')));
    }
}
