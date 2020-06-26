import {
  HTTP_INTERCEPTORS,
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { EMPTY, Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export class Error500Interceptor implements HttpInterceptor {
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler,
  ): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((errorResp: HttpErrorResponse) => {
        if (errorResp.status === 500) {
          // TODO: inject Error Service here and show error Messages
          console.error(errorResp);
          return EMPTY;
        }
        return throwError(errorResp);
      }),
    );
  }
}
