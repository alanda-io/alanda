import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';

@Injectable()
export class AlandaExceptionHandlingService {

    constructor() {}

    public handleError<T>(operation: string = 'operation', result?: T) {
        return (error: any): Observable<T> => {
            console.log(`${operation} failed: ${error.message}`);
            console.log(error);
            if (result !== undefined) {
                return of(result as T);
            } else {
                return throwError(error);
            }
        };
    }
}
