import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { ServerOptions } from '../models/serverOptions';
import { Observable } from 'rxjs';
import { AlandaListResult } from './models/listResult';
import { AlandaGroup } from './models/group';
import { catchError } from 'rxjs/operators';
import { AlandaPermission } from './models/permission';
import { AlandaRole } from './models/role';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';

@Injectable()
export class AlandaGroupApiService extends AlandaExceptionHandlingService {
  private readonly endpointUrl: string;

  constructor (private readonly http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/group';
  }

  getGroups (serverOptions: ServerOptions): Observable<AlandaListResult<AlandaGroup>> {
    return this.http.post<AlandaListResult<AlandaGroup>>(`${this.endpointUrl}/list`, serverOptions)
      .pipe(catchError(this.handleError<AlandaListResult<AlandaGroup>>('getGroups', null)));
  }

  getGroupByGuid (guid: number): Observable<AlandaGroup> {
    return this.http.get<AlandaGroup>(`${this.endpointUrl}/singleGroupById/${guid}`)
      .pipe(catchError(this.handleError<AlandaGroup>('getGroupByGuid', null)));
  }

  getGroupByName (name: string): Observable<AlandaGroup> {
    return this.http.get<AlandaGroup>(`${this.endpointUrl}/singleGroupByName/${name}`)
      .pipe(catchError(this.handleError<AlandaGroup>('getGroupByName', null)));
  }

  getGroupsForRole (roleName: string): Observable<AlandaGroup[]> {
    return this.http.get<AlandaGroup[]>(`${this.endpointUrl}/rolename/${roleName}`)
      .pipe(catchError(this.handleError<AlandaGroup[]>('getGroupsForRole', null)));
  }

  save (group: AlandaGroup): Observable<AlandaGroup> {
    return this.http.post<AlandaGroup>(`${this.endpointUrl}/create`, group)
      .pipe(catchError(this.handleError<AlandaGroup>('save')));
  }

  update (group: AlandaGroup): Observable<AlandaGroup> {
    return this.http.put<AlandaGroup>(`${this.endpointUrl}/update`, group)
      .pipe(catchError(this.handleError<AlandaGroup>('update')));
  }

  getEffectivePermissionsForGroup (guid: number): Observable<AlandaPermission[]> {
    return this.http.get<AlandaPermission[]>(`${this.endpointUrl}/permissions/effective/${guid}`)
      .pipe(catchError(this.handleError<AlandaPermission[]>('getEffectivePermissionsForGroup', [])));
  }

  updatePermissionsForGroup (guid: number, permissions: AlandaPermission[]): Observable<AlandaGroup> {
    return this.http.put<AlandaGroup>(`${this.endpointUrl}/permissions/update/${guid}`, permissions)
      .pipe(catchError(this.handleError<any>('updatePermissionsForGroup')));
  }

  updateRolesForGroup (guid: number, roles: AlandaRole[]): Observable<AlandaGroup> {
    return this.http.put<AlandaGroup>(`${this.endpointUrl}/roles/update/${guid}`, roles)
      .pipe(catchError(this.handleError<any>('updateRolesForGroup')));
  }
}
