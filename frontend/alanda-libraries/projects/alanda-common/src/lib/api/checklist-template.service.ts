import { Injectable } from '@angular/core';
import { ExceptionHandlingService } from '../services/exception-handling.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CheckListTemplate } from '../models/checklist.model';

@Injectable({
  providedIn: 'root',
})
export class ChecklistTemplateApiService extends ExceptionHandlingService {
  private checkListApiPath = '/app/checklist';

  constructor(private http: HttpClient) {
    super();
  }

  getAllCheckListTemplates(): Observable<CheckListTemplate[]> {
    return this.http.get<CheckListTemplate[]>(`${this.checkListApiPath}/templates`);
  }

  getCheckListTemplate(templateId: number): Observable<CheckListTemplate> {
    return this.http.get<CheckListTemplate>(`${this.checkListApiPath}/template/${templateId}`);
  }

  createCheckListTemplate(checkListTemplate: CheckListTemplate): Observable<void> {
    return this.http.post<void>(`${this.checkListApiPath}/template`, checkListTemplate);
  }

  updateCheckListTemplate(checkListTemplate: CheckListTemplate): Observable<void> {
    return this.http.put<void>(`${this.checkListApiPath}/template/${checkListTemplate.id}`, checkListTemplate);
  }

  deleteCheckListTemplate(checkListTemplateId: number): Observable<void> {
    return this.http.delete<void>(`${this.checkListApiPath}/template/${checkListTemplateId}`);
  }
}
