import { Injectable, Inject } from "@angular/core";
import { AppSettings, APP_CONFIG } from "../../models/AppSettings";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { PmcComment } from "../../models/PmcComment";
import { ExceptionHandlingService } from "../exceptionHandling.service";
import { catchError } from "rxjs/operators";

export type commentResponse = {
  comments: PmcComment[], 
  filterByRefObject: boolean, 
  refObjectIdName: string
}

export type commentPostBody = {
  subject: string,
  text: string,
  taskId: string,
  procInstId: string
}

export type replyPostBody = {
  text: string,
  taskId: string,
  procInstId: string,
  replyTo: number,
}

@Injectable({
  providedIn: 'root'
})
export class PmcCommentServiceNg extends ExceptionHandlingService{

  private endpointUrl : string;

  constructor(private http: HttpClient,
    @Inject(APP_CONFIG) config: AppSettings) {
    super();
    this.endpointUrl = config.API_ENDPOINT + "/comment";
  }

  getCommentsforPid(pid: string): Observable<commentResponse> {
    return this.http.get<commentResponse>(`${this.endpointUrl}/forProcessInstance/${pid}`).pipe(catchError(this.handleError('getCommentsforPid')));
  }

  postComment(comment: (commentPostBody | replyPostBody)): Observable<void> {
    return this.http.post<void>(`${this.endpointUrl}/post`, comment).pipe(catchError(this.handleError('postComment')));
  }

}