import { Injectable } from '@angular/core';
import { ExceptionHandlingService } from '../services/exception-handling.service';
import { HttpClient } from '@angular/common/http';
import { Observable, of, EMPTY } from 'rxjs';
import { CheckList, CheckListItemDefinition } from '../models/checklist.model';

@Injectable({
  providedIn: 'root',
})
export class ChecklistApiService extends ExceptionHandlingService {
  private checkListApiPath = '/app/checklist';

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

  constructor(private http: HttpClient) {
    super();
  }

  getCheckListsForUserTaskInstance(taskInstanceGuid: string): Observable<CheckList[]> {
    //return this.http.get<CheckList[]>(`${this.checkListApiPath}/userTask/${taskInstanceGuid}`);
    return of(this.checkLists);
  }

  setCheckListItemStatus(checkListId: number, itemKey: string, status: boolean): Observable<string> {
    return of('dummy-response');
    //return this.http.put<void>(`${this.checkListApiPath}/${checkListId}/${itemKey}`, status);
  }

  addCheckListItemToCheckList(checkListId: number, itemDefinition: CheckListItemDefinition): Observable<string> {
    return of('dummy-response');
    // return this.http.post<void>(`${this.checkListApiPath}/${checkListId}/definitions`, itemDefinition);
  }

  removeCheckListItemFromCheckList(checkListId: number, itemKey: string): Observable<string> {
    return of('dummy-response');
    //return this.http.delete<void>(`${this.checkListApiPath}/${checkListId}/definition/${itemKey}`);
  }
}
