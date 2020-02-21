import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { APP_CONFIG, AppSettings } from '../models/appSettings';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlandaExceptionHandlingService } from '../services/alandaExceptionHandling.service';

@Injectable({
  providedIn: 'root'
})
export class AlandaHistoryService extends AlandaExceptionHandlingService {

    private endpointUrl: string;

    constructor(private http: HttpClient, @Inject(APP_CONFIG) config: AppSettings) {
        super();
        this.endpointUrl = config.API_ENDPOINT + '/history';
    }

    search(filterOptions, pageNumber, pageSize): Observable<any[]> {
        const searchDto = {};
        for (const key of filterOptions) {
            if (filterOptions[key] !== '') {
                searchDto[key] = encodeURIComponent(filterOptions[key]);
            }
        }
        return this.http.post<any[]>(`${this.endpointUrl}/search/${pageNumber}/${pageSize}`, searchDto)
        .pipe(catchError(this.handleError('search', [])));
    }

}
