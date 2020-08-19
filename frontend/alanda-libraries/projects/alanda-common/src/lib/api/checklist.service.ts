import {Inject, Injectable} from '@angular/core';
import { ExceptionHandlingService } from '../services/exception-handling.service';
import { HttpClient } from '@angular/common/http';
import { Observable, of, EMPTY } from 'rxjs';
import { CheckList, CheckListItemDefinition } from '../models/checklist.model';
import {APP_CONFIG, AppSettings} from '../models/appSettings';

@Injectable({
  providedIn: 'root',
})
export class ChecklistApiService extends ExceptionHandlingService {

  private endpointUrl: string;

  constructor(@Inject(APP_CONFIG) config: AppSettings, private http: HttpClient) {
    super();
    this.endpointUrl = config.API_ENDPOINT + '/checklist';
  }

  checkLists: CheckList[] = [
    {
      id: 1,
      checkListItems: [
        {
          definition: {
            custom: false,
            displayText: 'Check 1.1',
            key: 'check-1-1',
            required: true
          },
          status: null
        },
        {
          definition: {
            custom: false,
            displayText: 'Check 1.2',
            key: 'check-1-2',
            required: false
          },
          status: null
        },
        {
          definition: {
            custom: false,
            displayText: 'Check 1.3',
            key: 'check-1-3',
            required: false
          },
          status: null
        }
      ],
      name: 'Checkliste 1'
    },
    {
      id: 2,
      checkListItems: [
        {
          definition: {
            custom: false,
            displayText: 'Check 2.1',
            key: 'check-2-1',
            required: true
          },
          status: null
        },
        {
          definition: {
            custom: false,
            displayText: 'Check 2.2',
            key: 'check-2-2',
            required: true
          },
          status: null
        },
        {
          definition: {
            custom: false,
            displayText: 'Check 2.3',
            key: 'check-2-3',
            required: false
          },
          status: null
        }
      ],
      name: 'Checkliste 2'
    }
  ];

  getCheckListsForUserTaskInstance(taskInstanceGuid: string): Observable<CheckList[]> {
    return this.http.get<CheckList[]>(`${this.endpointUrl}/userTask/${taskInstanceGuid}`);
  }

  setCheckListItemStatus(checkListId: number, itemKey: string, status: boolean): Observable<void> {
    return this.http.put<void>(`${this.endpointUrl}/${checkListId}/${itemKey}`, status);
  }

  addCheckListItemToCheckList(checkListId: number, itemDefinition: CheckListItemDefinition): Observable<void> {
    return this.http.post<void>(`${this.endpointUrl}/${checkListId}/definitions`, itemDefinition);
  }

  removeCheckListItemFromCheckList(checkListId: number, itemKey: string): Observable<void> {
    return this.http.delete<void>(`${this.endpointUrl}/${checkListId}/definition/${itemKey}`);
  }
}
