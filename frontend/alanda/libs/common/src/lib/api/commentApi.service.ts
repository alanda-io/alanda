import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AppSettings, APP_CONFIG } from '../models/appSettings';
import { Observable } from 'rxjs';
import { AlandaCommentResponse } from './models/commentResponse';
import { catchError } from 'rxjs/operators';
import { AlandaCommentPostBody } from './models/commenPostBody';
import { AlandaReplyPostBody } from './models/replyPostBody';
import { AlandaExceptionHandlingService } from '../services/exceptionHandling.service';
import { AlandaComment } from './models/comment';

@Injectable({
  providedIn: 'root',
})
export class AlandaCommentApiService extends AlandaExceptionHandlingService {
  private readonly endpointUrl: string;

  constructor(
    private readonly http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings,
  ) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/comment';
  }

  getCommentsforPid(pid: string): Observable<AlandaCommentResponse> {
    return this.http
      .get<AlandaCommentResponse>(
        `${this.endpointUrl}/forProcessInstance/${pid}`,
      )
      .pipe(
        catchError(
          this.handleError<AlandaCommentResponse>('getCommentsforPid'),
        ),
      );
  }

  postComment(
    comment: AlandaCommentPostBody | AlandaReplyPostBody,
  ): Observable<AlandaComment> {
    return this.http
      .post<AlandaComment>(`${this.endpointUrl}/post`, comment)
      .pipe(catchError(this.handleError<AlandaComment>('postComment')));
  }
}
