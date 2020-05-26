import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { AlandaRole } from './models/role';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';

@Injectable()
export class AlandaRoleApiService extends AlandaExceptionHandlingService {
  private readonly endpointUrl: string;

  constructor (private readonly http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/pmcrole';
  }

  getRoles (): Observable<AlandaRole[]> {
    return this.http.get<AlandaRole[]>(`${this.endpointUrl}/list`)
      .pipe(catchError(this.handleError<AlandaRole[]>('getRoles', [])));
  }

  getRolesForGroup (guid: number): Observable<AlandaRole[]> {
    return this.http.get<AlandaRole[]>(`${this.endpointUrl}/group/${guid}`)
      .pipe(catchError(this.handleError<AlandaRole[]>('getRolesForGroup', [])));
  }

  getRoleByGuid (guid: number): Observable<AlandaRole> {
    return this.http.get<AlandaRole>(`${this.endpointUrl}/single/${guid}`)
      .pipe(catchError(this.handleError<AlandaRole>('getRoleByGuid', null)));
  }

  getRoleByName (roleName: string): Observable<AlandaRole> {
    return this.http.get<AlandaRole>(`${this.endpointUrl}/single/name/${roleName}`)
      .pipe(catchError(this.handleError<AlandaRole>('getRoleByGuid', null)));
  }

  update (role: AlandaRole): Observable<AlandaRole> {
    return this.http.put<AlandaRole>(`${this.endpointUrl}/update`, role)
      .pipe(catchError(this.handleError<AlandaRole>('update')));
  }

  save (role: AlandaRole): Observable<AlandaRole> {
    return this.http.post<AlandaRole>(`${this.endpointUrl}/create`, role)
      .pipe(catchError(this.handleError<AlandaRole>('save')));
  }

  getRoleInstancesForProject (projectGuid: number, roleGuid: number): Observable<AlandaRole[]> {
    return this.http.get<AlandaRole[]>(`${this.endpointUrl}/role-instances/project/${projectGuid}/role/${roleGuid}`)
      .pipe(catchError(this.handleError<AlandaRole[]>('getRoleInstancesForProject')));
  }

  setRoleInstancesForProject (projectGuid: number, roleInstance: AlandaRole, source: string): Observable<AlandaRole[]> {
    const queryString = source ? `?source=${source}` : '';
    return this.http.post<AlandaRole[]>(`${this.endpointUrl}/role-instances/project/${projectGuid}/role${queryString}`, [roleInstance])
      .pipe(catchError(this.handleError<AlandaRole[]>('setRoleInstance')));
  }
}
