import { Injectable, Inject } from "@angular/core";
import { AppSettings, APP_CONFIG } from "../../models/AppSettings";
import { HttpClient } from "@angular/common/http";
import { Observable, of } from "rxjs";
import { PmcRole } from "../../models/PmcRole";
import { ExceptionHandlingService } from "../exceptionHandling.service";
import { catchError } from "rxjs/operators";


@Injectable({
  providedIn: 'root'
})
export class PmcRoleServiceNg extends ExceptionHandlingService{

  private endpointUrl : string;

  constructor(private http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + "/pmcrole";
  }

  getRoles(): Observable<PmcRole[]> {
    return this.http.get<PmcRole[]>(`${this.endpointUrl}/list`).pipe(catchError(this.handleError('getRoles', [])));
  }

  getRolesForGroup(guid: number): Observable<PmcRole[]>{
    return this.http.get<PmcRole[]>(`${this.endpointUrl}/group/${guid}`).pipe(catchError(this.handleError('getRolesForGroup', [])));
  }

  getRoleByGuid(guid: number): Observable<PmcRole>{
    return this.http.get<PmcRole>(`${this.endpointUrl}/single/${guid}`).pipe(catchError(this.handleError('getRoleByGuid', null)));
  }

  getRoleByName(roleName: string): Observable<PmcRole>{
    return this.http.get<PmcRole>(`${this.endpointUrl}/single/name/${roleName}`).pipe(catchError(this.handleError('getRoleByGuid', null)));
  }
  update(role: PmcRole): Observable<PmcRole> {
    return this.http.put<PmcRole>(`${this.endpointUrl}/update`, role).pipe(catchError(this.handleError('update')));
  }

  save(role: PmcRole): Observable<PmcRole> {
    return this.http.post<PmcRole>(`${this.endpointUrl}/create`, role).pipe(catchError(this.handleError('save')));
  }

  getRoleInstancesForProject(projectGuid: number, roleGuid: number): Observable<any[]>{
    return this.http.get<any[]>(`${this.endpointUrl}/role-instances/project/${projectGuid}/role/${roleGuid}`).pipe(catchError(this.handleError('getRoleInstancesForProject')));
  }

  setRoleInstancesForProject(projectGuid: number, roleInstance: any, source: string): Observable<any[]>{
    let queryString = source ? `?source=${source}` : '';
    return this.http.post<any>(`${this.endpointUrl}/role-instances/project/${projectGuid}/role${queryString}`, [roleInstance]).pipe(catchError(this.handleError('setRoleInstance')));
  }
}
