import { Injectable, Inject } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AlandaUser } from './models/user';
import { HttpClient, HttpParams } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { AlandaListResult } from './models/listResult';
import { catchError, tap } from 'rxjs/operators';
import { AlandaRole } from './models/role';
import { AlandaGroup } from './models/group';
import { AlandaPermission } from './models/permission';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { ServerOptions } from '../models/serverOptions';

@Injectable()
export class AlandaUserApiService extends AlandaExceptionHandlingService {
  private readonly endpointUrl: string;
  public user$: BehaviorSubject<AlandaUser> = new BehaviorSubject(null);

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/user';
  }

  getUsers(
    serverOptions: ServerOptions,
  ): Observable<AlandaListResult<AlandaUser>> {
    return this.http
      .post<AlandaListResult<AlandaUser>>(
        `${this.endpointUrl}/repo`,
        serverOptions,
      )
      .pipe(
        catchError(this.handleError<AlandaListResult<AlandaUser>>('getUsers')),
      );
  }

  getUser(guid: number): Observable<AlandaUser> {
    return this.http
      .get<AlandaUser>(`${this.endpointUrl}/repo/${guid}`)
      .pipe(catchError(this.handleError<AlandaUser>('getUser')));
  }

  getUserByLogin(login: string): Observable<AlandaUser> {
    return this.http
      .get<AlandaUser>(`${this.endpointUrl}/single/${login}`)
      .pipe(catchError(this.handleError<AlandaUser>('getUserByLogin')));
  }

  updateUser(user: AlandaUser): Observable<AlandaUser> {
    return this.http
      .put<AlandaUser>(`${this.endpointUrl}/repo/update`, user)
      .pipe(catchError(this.handleError<AlandaUser>('updateUser')));
  }

  save(user: AlandaUser): Observable<void> {
    return this.http
      .post<void>(`${this.endpointUrl}/repo/create`, user)
      .pipe(catchError(this.handleError<void>('save')));
  }

  getEffectiveRolesForUser(userGuid: number): Observable<AlandaRole[]> {
    return this.http
      .get<AlandaRole[]>(`${this.endpointUrl}/roles/effective/${userGuid}`)
      .pipe(
        catchError(this.handleError<AlandaRole[]>('getEffectiveRolesForUser')),
      );
  }

  updateRolesForUser(userGuid: number, roles: AlandaRole[]): Observable<void> {
    return this.http
      .put<void>(`${this.endpointUrl}/roles/update/${userGuid}`, roles)
      .pipe(catchError(this.handleError<void>('updateRolesForUser')));
  }

  getGroupsForUser(login: string): Observable<AlandaGroup[]> {
    return this.http
      .get<AlandaGroup[]>(`${this.endpointUrl}/groups/${login}`)
      .pipe(catchError(this.handleError<AlandaGroup[]>('getGroupsForUser')));
  }

  getEffectivePermissionsForUser(
    userGuid: number,
  ): Observable<AlandaPermission[]> {
    return this.http
      .get<AlandaPermission[]>(
        `${this.endpointUrl}/permissions/effective/${userGuid}`,
      )
      .pipe(
        catchError(
          this.handleError<AlandaPermission[]>(
            'getEffectivePermissionsForUser',
          ),
        ),
      );
  }

  updatePermissionsForUser(
    userGuid: number,
    permissions: AlandaPermission[],
  ): Observable<void> {
    return this.http
      .put<void>(
        `${this.endpointUrl}/permissions/update/${userGuid}`,
        permissions,
      )
      .pipe(catchError(this.handleError<void>('updatePermissionsForUser')));
  }

  getCurrentUser(): Observable<AlandaUser> {
    return this.http
      .get<AlandaUser>(`${this.endpointUrl}/current`)
      .pipe(catchError(this.handleError<AlandaUser>('getCurrentUser')));
  }

  runAsUser(userName: string): Observable<AlandaUser> {
    return this.http
      .post<AlandaUser>(`${this.endpointUrl}/runas/${userName}`, {})
      .pipe(catchError(this.handleError<AlandaUser>('runAsUser')));
  }

  releaseRunAs(): Observable<AlandaUser> {
    return this.http
      .post<AlandaUser>(`${this.endpointUrl}/release`, {})
      .pipe(catchError(this.handleError<AlandaUser>('releaseRunAs')));
  }

  getUsersByGroupId(groupId: number): Observable<AlandaUser[]> {
    return this.http
      .get<AlandaUser[]>(
        `${this.endpointUrl}/repo/getUsersByGroupId/${groupId}`,
      )
      .pipe(catchError(this.handleError<AlandaUser[]>('runAsUser')));
  }

  getUsersForRole(roleId: number): Observable<AlandaUser[]> {
    return this.http
      .get<AlandaUser[]>(`${this.endpointUrl}/role/${roleId}`)
      .pipe(catchError(this.handleError<AlandaUser[]>('getUsersForRole')));
  }

  searchUsers(text: string, groupName: string): Observable<AlandaUser[]> {
    let params = new HttpParams();
    if (text) {
      params = params.set('text', text);
    }
    if (groupName) {
      params = params.set('groupName', groupName);
    }
    return this.http
      .get<AlandaUser[]>(`${this.endpointUrl}/search`, { params })
      .pipe(catchError(this.handleError<AlandaUser[]>('searchUsers')));
  }
}
