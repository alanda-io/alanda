import { Injectable, Inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, ReplaySubject } from 'rxjs';
import { APP_CONFIG, AppSettings } from "../models/appSettings";
import { RefObject } from "../models/refObject";
@Injectable()
export class RefObjectService {

  private endpointUrl : string;

  constructor(@Inject(APP_CONFIG) config: AppSettings,
              private http: HttpClient) {
    this.endpointUrl = config.API_ENDPOINT + '/refobjects';
  }

   // ReplaySubject: buffers x next() values, 
   // so if a subscribe is called after a next() call already happend, the change is still detected
  private refObject = new ReplaySubject<RefObject>(1);
  refObjectAnnounced$: Observable<RefObject> = this.refObject.asObservable();

  announceRefObject(refObject: RefObject) {
    this.refObject.next(refObject);
  }

  public autocomplete(type: string, searchTerm: string): Observable<RefObject[]> {
    return this.http.get<RefObject[]>(`${this.endpointUrl}/${type}?search=${searchTerm}`);
  }

  public getByIdName(type: string, idName: string): Promise<RefObject> {
    return this.http.get(`${this.endpointUrl}/${type}/${idName}`)
      .toPromise()
      .then(resp => resp as RefObject)
      .catch(this.handleError);
  }

  public setStateByIdName(type: string, idName: string, state: string) {
    return this.http.put(`${this.endpointUrl}/${type}/${idName}`, state)
    .toPromise()
    .then(resp => resp as RefObject)
    .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.log('An error occured in RefObjectService', error);
    return Promise.reject(error.message || error);
  }

  public 

}