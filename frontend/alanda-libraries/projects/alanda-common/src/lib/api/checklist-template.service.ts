import {Inject, Injectable} from '@angular/core';
import { ExceptionHandlingService } from '../services/exception-handling.service';
import { HttpClient } from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {CheckListItemBackend, CheckListItemDefinition, CheckListTemplate} from '../models/checklist.model';
import {APP_CONFIG, AppSettings} from '../models/appSettings';

@Injectable({
  providedIn: 'root',
})
export class ChecklistTemplateApiService extends ExceptionHandlingService {

  private endpointUrl: string;

  constructor(@Inject(APP_CONFIG) config: AppSettings, private http: HttpClient) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/checklist';
  }

  private clTemplate: CheckListTemplate[] = [
    {
      id: 1,
      name: 'Template1',
      itemBackend: 'DB',
      userTasks: ['ut1', 'ut2', 'ut2'],
      itemDefinitions: [
        {
          custom: false,
          key: 't-check-1-1',
          displayText: 'T-Check 1.1',
          required: true,
        },
        {
          custom: false,
          key: 't-check-1-2',
          displayText: 'T-Check 1.2',
          required: true,
        }
      ]
    },
    {
      id: 1,
      name: 'Template2',
      itemBackend: 'DB',
      userTasks: ['ut1', 'ut2', 'ut2'],
      itemDefinitions: [
        {
          custom: false,
          key: 't-check-2-1',
          displayText: 'T-Check 2.1',
          required: true,
        },
        {
          custom: false,
          key: 't-check-2-2',
          displayText: 'T-Check 2.2',
          required: true,
        }
      ]
    }
  ];

  getAllCheckListTemplates(): Observable<CheckListTemplate[]> {
    //return of(this.clTemplate);
    return this.http.get<CheckListTemplate[]>(`${this.endpointUrl}/templates`);
  }

  getCheckListTemplate(templateId: number): Observable<CheckListTemplate> {
    return this.http.get<CheckListTemplate>(`${this.endpointUrl}/template/${templateId}`);
  }

  createCheckListTemplate(checkListTemplate: CheckListTemplate): Observable<void> {
    return this.http.post<void>(`${this.endpointUrl}/template`, checkListTemplate);
  }

  updateCheckListTemplate(checkListTemplate: CheckListTemplate): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/template/${checkListTemplate.id}`, checkListTemplate);
  }

  deleteCheckListTemplate(checkListTemplateId: number): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/template/${checkListTemplateId}`);
  }
}
