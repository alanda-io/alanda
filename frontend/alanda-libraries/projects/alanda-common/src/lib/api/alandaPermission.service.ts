import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { AlandaPermission } from './models/alandaPermission';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/alandaExceptionHandling.service';

@Injectable()
export class AlandaPermissionService extends AlandaExceptionHandlingService {

  private endpointUrl: string;

  constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/permission';
  }

  getPermissions(): Observable<AlandaPermission[]> {
    return this.http.get<AlandaPermission[]>(`${this.endpointUrl}/list`)
    .pipe(catchError(this.handleError('getPermissions', [])));
  }

  save(permission: AlandaPermission): Observable<AlandaPermission> {
    return this.http.post<AlandaPermission>(`${this.endpointUrl}/create`, permission)
    .pipe(catchError(this.handleError('save')));
  }

  update(permission: AlandaPermission): Observable<AlandaPermission> {
    return this.http.put<AlandaPermission>(`${this.endpointUrl}/update`, permission)
    .pipe(catchError(this.handleError('update')));
  }

  getPermissionByGuid(guid: number): Observable<AlandaPermission>{
    return this.http.get<AlandaPermission>(`${this.endpointUrl}/single/${guid}`)
    .pipe(catchError(this.handleError('getPermissionByGuid', null)));
  }

}
