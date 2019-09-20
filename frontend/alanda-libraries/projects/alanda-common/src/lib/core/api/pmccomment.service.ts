import { Injectable, Inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { ExceptionHandlingService } from "../services/exception-handling.service";
import { catchError } from "rxjs/operators";
import { APP_CONFIG, AppSettings } from "../../models/appSettings";
import { PmcComment } from "../../components/comments/models/pmcComment";

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