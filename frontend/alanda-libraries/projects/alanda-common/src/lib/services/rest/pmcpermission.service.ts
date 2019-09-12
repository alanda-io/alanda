import { Injectable, Inject } from "@angular/core";
import { AppSettings, APP_CONFIG } from "../../models/AppSettings";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { PmcPermission } from "../../models/PmcPermission";
import { ExceptionHandlingService } from "../exceptionHandling.service";
import { catchError } from "rxjs/operators";


@Injectable({
  providedIn: 'root'
})
export class PmcPermissionServiceNg extends ExceptionHandlingService{

  private endpointUrl : string;

  constructor(private http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + "/permission";
  }

  getPermissions(): Observable<PmcPermission[]> {
    return this.http.get<PmcPermission[]>(`${this.endpointUrl}/list`).pipe(catchError(this.handleError('getPermissions', [])));
  }

  save(permission: PmcPermission): Observable<any> {
    return this.http.post<any>(`${this.endpointUrl}/create`, permission).pipe(catchError(this.handleError('save')));
  }

  update(permission: PmcPermission): Observable<any> {
    return this.http.put<any>(`${this.endpointUrl}/update`, permission).pipe(catchError(this.handleError('update')));
  }

  getPermissionByGuid(guid: number): Observable<PmcPermission>{
    return this.http.get<PmcPermission>(`${this.endpointUrl}/single/${guid}`).pipe(catchError(this.handleError('getPermissionByGuid', null)));
  }

}