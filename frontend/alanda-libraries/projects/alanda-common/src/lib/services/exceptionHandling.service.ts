import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';

// @TODO the whole service should be replaced by a function. the function it self still brings no real value.
// Do we really want to handel all errors here? => NO! consider HTTPInterzepters for the general cases (500, 403, 401)
@Injectable()
export class AlandaExceptionHandlingService {
  constructor() {}

  public handleError<T>(operation: string = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.log(`${operation} failed: ${error.message}`);
      console.log(error);
      if (result !== undefined) {
        return of(result);
      } else {
        return throwError(error);
      }
    };
  }
}
