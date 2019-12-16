import { Injectable, Inject } from "@angular/core";
import { AppSettings, APP_CONFIG } from "../../models/AppSettings";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { ListResult } from "../../models/ListResult";
import { PmcGroup } from "../../models/PmcGroup";
import { PmcPermission } from "../../models/PmcPermission";
import { PmcRole } from "../../models/PmcRole";
import { ExceptionHandlingService } from "../exceptionHandling.service";
import { catchError } from "rxjs/operators";


@Injectable({
  providedIn: 'root'
})
export class PmcGroupServiceNg extends ExceptionHandlingService{

  private endpointUrl : string;

  constructor(private http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + "/group";
  }

  getGroups(serverOptions: any): Observable<ListResult<PmcGroup>> {
    return this.http.post<ListResult<PmcGroup>>(`${this.endpointUrl}/list`, serverOptions).pipe(catchError(this.handleError<ListResult<PmcGroup>>('getGroups', null)));
  }

  getGroupByGuid(guid: number): Observable<PmcGroup> {
    return this.http.get<PmcGroup>(`${this.endpointUrl}/singleGroupById/${guid}`).pipe(catchError(this.handleError<PmcGroup>('getGroupByGuid', null)));
  }

  getGroupByName(name: string): Observable<PmcGroup> {
    return this.http.get<PmcGroup>(`${this.endpointUrl}/singleGroupByName/${name}`).pipe(catchError(this.handleError<PmcGroup>('getGroupByName', null)));
  }

  getGroupsForRole(roleName: string): Observable<PmcGroup[]> {
    return this.http.get<PmcGroup[]>(`${this.endpointUrl}/rolename/${roleName}`).pipe(catchError(this.handleError<PmcGroup[]>('getGroupsForRole', null)));
  }

  save(group: PmcGroup): Observable<PmcGroup> {
    return this.http.post<PmcGroup>(`${this.endpointUrl}/create`, group).pipe(catchError(this.handleError<PmcGroup>('save')));
  }

  update(group: PmcGroup): Observable<PmcGroup> {
    return this.http.put<PmcGroup>(`${this.endpointUrl}/update`, group).pipe(catchError(this.handleError<PmcGroup>('update')));
  }

  getEffectivePermissionsForGroup(guid: number): Observable<PmcPermission[]> {
    return this.http.get<PmcPermission[]>(`${this.endpointUrl}/permissions/effective/${guid}`).pipe(catchError(this.handleError<PmcPermission[]>('getEffectivePermissionsForGroup', [])));
  }

  updatePermissionsForGroup(guid: number, permissions: PmcPermission[]): Observable<any> {
    return this.http.put<any>(`${this.endpointUrl}/permissions/update/${guid}`, permissions).pipe(catchError(this.handleError<any>('updatePermissionsForGroup')));
  }

  updateRolesForGroup(guid: number, roles: PmcRole[]): Observable<any> {
    return this.http.put<any>(`${this.endpointUrl}/roles/update/${guid}`, roles).pipe(catchError(this.handleError<any>('updateRolesForGroup')));
  }
}
