import { Injectable } from '@angular/core';
import { ExceptionHandlingService } from '../services/exception-handling.service';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CheckList, CheckListItemDefinition } from '../models/checklist.model';

@Injectable({
  providedIn: 'root',
})
export class ChecklistApiService extends ExceptionHandlingService {
  private checkListApiPath = '/app/checklist';

  constructor(private http: HttpClient) {
    super();
  }

  getCheckListsForUserTaskInstance(taskInstanceGuid: string): Observable<CheckList[]> {
    return this.http.get<CheckList[]>(`${this.checkListApiPath}/userTask/${taskInstanceGuid}`);
  }

  setCheckListItemStatus(checkListId: number, itemKey: string, status: boolean): Observable<void> {
    return this.http.put<void>(`${this.checkListApiPath}/${checkListId}/${itemKey}`, status);
  }

  addCheckListItemToCheckList(checkListId: number, itemDefinition: CheckListItemDefinition): Observable<void> {
    return this.http.post<void>(`${this.checkListApiPath}/${checkListId}/definitions`, itemDefinition);
  }

  removeCheckListItemFromCheckList(checkListId: number, itemKey: string): Observable<void> {
    return this.http.delete<void>(`${this.checkListApiPath}/${checkListId}/definition/${itemKey}`);
  }
}
